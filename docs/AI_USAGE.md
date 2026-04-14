# AI Usage in Development

## Objective

This project was developed through SDD, that is, Spec-Driven Development, where the developer acts in the role of software architect and the AI fulfills these agreed requirements, optimizing the time and quality of solution development.

## How I Use It

Used the [copilot-agents-dojo](https://github.com/andreaswasita/copilot-agents-dojo) framework with a few targeted modifications, but the workflow remains the same.

### Agents

First, it is necessary to define the workflow that will be used with AI and, for that, I start by creating personas that act as collaborative teammates. For this application, the following were created:

- The [architect](#): a persona that helps organize and make decisions between solutions by considering trade-offs self-discovered by AI, along with points and comments made by a human collaborator.
It is also responsible for organizing the personas (agents) that follow.

- The [security engineer](#): a persona responsible for weighing security concerns, vulnerability exploration, and failure mitigation during specification design.

- The [software engineer](#): a persona that effectively implements the specifications.

- The [technical program manager](#): a persona that organizes activities while considering deadlines, priority order, and the fulfillment of a deadline or objective. The result from this agent is an organized board indicating the order in which activities should be carried out to satisfy a business demand.

- The [test engineer](#): a persona that plans and implements the testing scope of the activities carried out by the software engineer.
It is also responsible for implementing quality gates to keep the project at a high standard.

### Skills

After defining the collaborators that will work with me, I define the skills these personas can have and also how they will interact with each other.

At this stage, the skills are defined and mostly orchestrated through [copilot-instructions](../.github/copilot-instructions.md), where I instruct the system to load all skills into context as needed.

### Feedback Loop

To finish my workflow, I feed the AI with its own output. In practice, I ask that, within its workflow, it be able to create memories or document lessons learned.

This technique allows the reasoning flow of the technology to be tracked and also supports the creation of new skills.

For this project, this technique can be followed in `./tasks/lessons.md` and `./tasks/todo.md`.

## Limits and Criteria

I try to keep the context usage rate below 60% to avoid AI hallucinations, and at each prompt or interaction I evaluate what line of reasoning is being developed.

## Validation Process

In addition to the `./tasks/lessons.md` and `./tasks/todo.md` files, every modification made is tracked.

Through well-structured commits or PRs, I try to make this follow-up simpler and more objective, ensuring that the AI reaches deterministic conclusions.