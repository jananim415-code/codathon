# ProjectSphere algorithms

The intelligence engine uses deterministic formulas so that every result can
be explained to a team.

## Contribution score

For a user, metrics are summed across the project's contribution records:

```text
effectiveChurn = max(0, codeChurn - trivialChanges)
score =
  normalize(commits, maxCommits) * 30
  + normalize(effectiveChurn, maxChurn) * 25
  + normalize(filesChanged, maxFiles) * 15
  + normalize(pullRequests, maxPullRequests) * 20
  + taskCompletionRate * 10 / 100
```

`normalize(value, maxValue)` is `min(1, value / maxValue)`, with a minimum
denominator of one. The weighted components therefore sum to at most 100, and
the final score is capped to `[0, 100]`.
Task completion is calculated only from tasks assigned to that user. In
development mode, missing contribution records receive deterministic demo
records; production scores missing activity as zero rather than inventing data.

## Free-rider detection

The detector calculates the team mean and population standard deviation of
member scores:

```text
z = (memberScore - teamMean) / standardDeviation
```

A member is flagged when `z <= -1.0`. Teams with zero standard deviation do not
produce a flag. The API calls these results “potential low-contribution
members”; the score is not an accusation.

## Project health

```text
health =
  commitTrend * 0.40
  + taskCompletionRate * 0.40
  + deadlineScore * 0.20
```

The current commit trend is derived from the available GitHub commit list:
`min(100, commitCount * 20)`. Deadline score is 100 when more than 14 days
remain, 75 for 8–14 days, 50 for 0–7 days, 0 when overdue, and 100 when no
deadline exists. Classification is `HEALTHY` (80+), `MODERATE` (60–79),
`AT_RISK` (40–59), or `CRITICAL` (below 40).

## Recommendations

Recommendations are rule-based: low task completion (<50), low health (<60),
and any flagged member each add a targeted message. A documentation/GitHub
alignment reminder is always included.
