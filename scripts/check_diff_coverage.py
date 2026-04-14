#!/usr/bin/env python3

from __future__ import annotations

import argparse
import subprocess
import sys
import xml.etree.ElementTree as element_tree
from collections import defaultdict
from pathlib import Path


def run_command(*args: str) -> str:
    return subprocess.check_output(args, text=True).strip()


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="Validate JaCoCo coverage on changed executable Java lines."
    )
    parser.add_argument("--base-ref", required=True, help="Git reference used as PR base.")
    parser.add_argument(
        "--jacoco-report",
        default="build/reports/jacoco/test/jacocoTestReport.xml",
        help="Path to the JaCoCo XML report.",
    )
    parser.add_argument(
        "--threshold",
        type=float,
        default=98.0,
        help="Minimum changed-line coverage percentage.",
    )
    return parser.parse_args()


def parse_diff(base_ref: str) -> dict[str, set[int]]:
    merge_base = run_command("git", "merge-base", "HEAD", base_ref)
    diff_output = run_command(
        "git",
        "diff",
        "--unified=0",
        "--no-color",
        merge_base,
        "HEAD",
        "--",
        "src/main/java",
    )

    changed_lines: dict[str, set[int]] = defaultdict(set)
    current_file: str | None = None
    new_line_number = 0

    for raw_line in diff_output.splitlines():
        if raw_line.startswith("+++ b/"):
            current_file = raw_line[6:]
            continue

        if raw_line.startswith("@@"):
            hunk_header = raw_line.split("@@", maxsplit=2)[1].strip()
            new_range = hunk_header.split(" ")[1]
            start_text = new_range[1:].split(",", maxsplit=1)[0]
            new_line_number = int(start_text)
            continue

        if current_file is None or not current_file.endswith(".java"):
            continue

        if raw_line.startswith("+") and not raw_line.startswith("+++"):
            changed_lines[current_file].add(new_line_number)
            new_line_number += 1
            continue

        if raw_line.startswith("-") and not raw_line.startswith("---"):
            continue

        if raw_line.startswith(" "):
            new_line_number += 1

    return changed_lines


def parse_jacoco_report(report_path: Path) -> tuple[dict[str, set[int]], dict[str, set[int]]]:
    tree = element_tree.parse(report_path)
    root = tree.getroot()

    executable_lines: dict[str, set[int]] = defaultdict(set)
    covered_lines: dict[str, set[int]] = defaultdict(set)

    for package in root.findall("package"):
        package_name = package.attrib.get("name", "")
        for source_file in package.findall("sourcefile"):
            source_name = source_file.attrib["name"]
            source_path = Path("src/main/java") / package_name / source_name
            normalized_path = source_path.as_posix()

            for line in source_file.findall("line"):
                line_number = int(line.attrib["nr"])
                covered_instructions = int(line.attrib["ci"])
                missed_instructions = int(line.attrib["mi"])

                if covered_instructions == 0 and missed_instructions == 0:
                    continue

                executable_lines[normalized_path].add(line_number)
                if covered_instructions > 0:
                    covered_lines[normalized_path].add(line_number)

    return executable_lines, covered_lines


def main() -> int:
    args = parse_args()
    changed_lines = parse_diff(args.base_ref)
    executable_lines, covered_lines = parse_jacoco_report(Path(args.jacoco_report))

    total_executable = 0
    total_covered = 0
    per_file_summaries: list[str] = []

    for file_path, changed in sorted(changed_lines.items()):
        executable_changed = sorted(changed & executable_lines.get(file_path, set()))
        if not executable_changed:
            continue

        covered_changed = set(executable_changed) & covered_lines.get(file_path, set())
        file_total = len(executable_changed)
        file_covered = len(covered_changed)
        total_executable += file_total
        total_covered += file_covered
        coverage_percentage = (file_covered / file_total) * 100
        per_file_summaries.append(
            f"{file_path}: {file_covered}/{file_total} lines covered ({coverage_percentage:.2f}%)"
        )

    if total_executable == 0:
        print("No executable Java lines changed. Diff coverage gate passed.")
        return 0

    total_percentage = (total_covered / total_executable) * 100

    print("Changed-line coverage summary:")
    for summary in per_file_summaries:
        print(f" - {summary}")
    print(
        f"Total changed-line coverage: {total_covered}/{total_executable} "
        f"({total_percentage:.2f}%)"
    )

    if total_percentage < args.threshold:
        print(
            f"Changed-line coverage is below the required {args.threshold:.2f}% threshold.",
            file=sys.stderr,
        )
        return 1

    return 0


if __name__ == "__main__":
    raise SystemExit(main())