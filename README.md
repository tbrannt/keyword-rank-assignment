# keyword-rank-assignment

## General Alrorithm

To estimate the popolarity of a keyword on amazon the an initial request to the amazon completion api is made.
The length of this suggestions list is a first small contributor to the score (initial score = number of suggestions). 
Then for each suggestion there's another request made without the keyword. If our initial keyword is returned this contributes positively to our total score.

## Assumptions

- the completion API should allow up to 10 parallel requests to their API via the same IP without significant rate limiting
- the order of the returned suggestions is not really important

## Hint correctness

The order probably does imply that a search term is more significant the further it is on top. But with thousands of possible ways to complete a key word 
where we only see the top 10 it is much more meaningful if we see that key word at all than where it is in the top 10

## How Precise

It is just a rough estimate. It can be further improved by doing multiple recursive rounds and overall more requests to the amazon completion API.

