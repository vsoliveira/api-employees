package com.company.hr.architecture;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "com.company.hr", importOptions = ImportOption.DoNotIncludeTests.class)
class ModularArchitectureTest {

    @ArchTest
    static final ArchRule employeesLayeringIsEnforced = layeredArchitecture()
            .consideringOnlyDependenciesInLayers()
            .layer("Shared").definedBy("com.company.hr.shared..")
            .layer("EmployeesDomain").definedBy("com.company.hr.employees.domain..")
            .layer("EmployeesApplication").definedBy("com.company.hr.employees.application..")
            .layer("EmployeesInfrastructure").definedBy("com.company.hr.employees.infrastructure..")
            .whereLayer("Shared").mayOnlyBeAccessedByLayers(
                    "EmployeesDomain",
                    "EmployeesApplication",
                    "EmployeesInfrastructure"
            )
            .whereLayer("EmployeesDomain").mayOnlyBeAccessedByLayers(
                    "EmployeesApplication",
                    "EmployeesInfrastructure"
            )
            .whereLayer("EmployeesDomain").mayOnlyAccessLayers("Shared")
            .whereLayer("EmployeesApplication").mayOnlyBeAccessedByLayers("EmployeesInfrastructure")
            .whereLayer("EmployeesApplication").mayOnlyAccessLayers("EmployeesDomain", "Shared")
            .whereLayer("EmployeesInfrastructure").mayOnlyAccessLayers(
                    "EmployeesApplication",
                    "EmployeesDomain",
                    "Shared"
            );

    @ArchTest
    static final ArchRule sharedKernelDoesNotDependOnEmployees = noClasses()
            .that().resideInAPackage("com.company.hr.shared..")
            .should().dependOnClassesThat().resideInAPackage("com.company.hr.employees..");
}