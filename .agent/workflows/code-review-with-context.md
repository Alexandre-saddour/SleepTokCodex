---
description: code review with IMPLEMENTATION_PLAN as context
---

As a source of guidance, check IMPLEMENTATION_PLAN.md because maybe there are placeholders or WIP code that you don't want to fix right now because it will be done later

If the current branch is the main branch
Then 
1- do a code review of the whole code base.
2- find bugs, optimisations, cleaning, architecture improvement, everything.
3- Create a new branch, proceed with fixes and push

OTHERWISE, if the current branch IS NOT the main branch

Then
1- Look at the files in the current branch that differ from the main branch and do a code review of those files.
2- find bugs, optimisations, cleaning, architecture improvement, everything.
3- Proceed with fixes and push on the current branch

