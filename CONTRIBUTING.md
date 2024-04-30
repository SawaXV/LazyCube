# Development Guidelines

## Branches

### Major Branches

There are 4 major branches. All commits to these branches **should** be made via merge requests from tasks in Jira.

- `main` - holds the most up to date, **production ready** version of the application.
- `cube-logic` - holds the most up to date and working version of the internal logic for cube colour and face alignment.
  - Note: This branch has automated CI JUnit testing, therefore, this branch should be fully tested and working at all times.
- `deep-learning` - holds research notebooks and scripts for detectron2 and tensorflow. Holds the main training script for training a production ready TensorFlow Lite model for the application.
- `android-app` - holds the most up to date version of the Android front-end for the project.

## Feature Branches

All feature branches should branch from their respective major branch (not main), and should represent the work completed from one of the tasks within Jira. If the work is too big for one branch (and one merge request), consider splitting the task up into smaller tasks in Jira.

Completing tasks this way allows members to review your work to make sure there aren't any glaring errors being made. It also allows for easy parallel work between sub-team members working on the same part of the project.

**All branches should follow the format: [u/m/l]-[initials]-[branch-description], where u/m/l are the different sub-teams in the project.**

### Merge Requests

**Size** - Merge requests shouldn't be too big *(note the previous points about feature branches)*. Otherwise, reviews from team members might take too long. In this case, oversights in reviewing the code may happen.

**Title** - Prefix the title of the merge request with the respective task ID from Jira. For example: `LAZ-74 Complete corner logic validation`. GitLab will automatically link the merge request with the task in Jira.

**Descriptions** - Give the merge request's description a good explanation of what the changes in the merge request are. Don't make this too exhaustive, however. If you find you're writing a lot to explain what you've done, consider splitting future Jira tasks up, so your merge requests don't become too big.

**Labels** - Apply appropriate labels to your merge request to show others what it is about. Currently, there are labels for: Android, Bug fix, Deep Learning, Feature, Logic, Refactor, Tests, Repo Update (for READMEs, pipelines, etc.)

**Review** - Always assign the merge request to someone other than yourself. Get this other team member (within your sub-team or not) to review your code and approve the merge request when everything is satisfactory, and the related Jira task has been completed.

## Commits

Keeping good commit messages will help us all quickly understand changes made by other members easily.

- Use the imperative mood in the subject line (command/request)
  - e.g "If applied, this commit will \<your subject line here>\"
  - `Add ...`
  - `Update ...`
  - `Fix ...`
  - `Refactor ...`
  - Do not end the subject line with a full stop
- Write a descriptive body for the commit message, so members can understand what your changes are.
  - Explain what and why. Leave out how a change has been made.
  - Separate the subject line from the body with a blank line.
  - Limit the subject line to 50 characters
  - Capitalise the subject line and each paragraph
  - Wrap the body at 72 characters


## Code style

Keeping the style of code throughout the project is very important for readability and maintainability.

For Java, please refer to this [Google Style Guide](https://google.github.io/styleguide/javaguide.html) for a comprehensive guideline.
For Python, please refer to this [Google Stlye Guide](https://google.github.io/styleguide/pyguide.html)

### General guidelines for Java

In general, follow these basic styles to keep our programming in Java consistent

#### Formatting

Keep braces to the same line as the statement. For example:

``` Java
if (condition) {
    ...
}

// NOT
if (condition)
{
    ...
}
```

As per Bob's Coding Convention, keep the number of characters per line <= 80.

#### Classes

Classes (interfaces, enums, etc.) should be `CamelCase`, always beginning with a capital. For example:

``` Java
public class MyClass extends Foo implements MyInterface {
    ...
}
```

#### Methods

Methods should be `camelCase`, always beginning with a lowercase. For example:

``` Java
public void fooBar() {
    ...
}
```

#### Variables

Member variables should also be `camelCase`, always beginning with a lowercase. For example:

``` Java
private int myVariable = 5;
```

Constants should be in capital `SNAKE_CASE`. The exception to this rule is if the data structure of the constant variable is mutable. For example:

``` Java
private final int CONSTANT_VALUE = 99;
```

But for mutable data structures (e.g. lists), fall back to the usual convention:

``` Java
public final ArrayList<String> myInts;
```

#### Extras

Always use `@Override`, when applicable:

``` Java
@Override
public int myOverrideMethod() {
    ...
}
```
