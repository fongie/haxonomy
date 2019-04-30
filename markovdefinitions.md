# Definitions

## State

State. Modelling the current knowledge about the website that the hacker possesses.

If f.e we have 4 terms t1, t2, t3, t4, and there is a question connected to each which can be answered by "yes", "no", and "dont know", then the current state is (Qt1, Qt2, Qt3, Qt4) where Qtx is the answer to the question.

When we know nothing, the state would be (dont know, dont know, dont know, dont know).

If we ask the question belonging to t1 and the answer is "yes", the state is: (yes, dont know, dont know, dont know)

## Actions

Actions. An action for us is asking the hacker to define one of the questions that are currently "dont know".

Example:

We are in our first state where (Qt1,Qt2,Qt3,Qt4) is (dk, dk, dk, dk).

One action we can take is to ask Qt1. If the answer is yes, we go to state (yes, dk, dk, dk), otherwise, (no, dk, dk, dk).

## Transition function

When doing an action (asking a question), the transition function is the probability that the hacker will answer "yes" or "no".

Our transition function will be: #reportscontainingTx / #totalreports

Sources of error: How often Tx appears in total reports might not be representative of how often this element will appear in a website

Formal example with 4 terms going from beginning to asking Qt1 and ending up with answer "yes":

T( (dk,dk,dk,dk), Qt1, (yes,dk,dk,dk) ) = #reportswithT1 / #totalreports

## Immediate reward

This is our "cost" as well. How much does it cost to perform an action.

Our cost is _Ht_ where H is the hourly rate input by the hacker (how much their time is worth) and _t_ is a time factor.

The time factor can be 1 minute, 60 minutes, or 6\*60 = 360 minutes.

## Expected reward

If we ask this question (make this action) in this state (with this knowledge) and the answer is "yes", what is the average bounty for reports applicable
(we want to perform the action with the highest expected reward probably)

## Implementation

Man går igenom frågeflödet, men man har hela tiden en topplista på sårbarheter som är applicerbara "just nu".
T.ex XSS 50% SQL Inject 25% osv...
som baseras på hur många rapporter som är taggade med dessa sårbarheter tillsammans med de taggar vi redan svarat på
Om en viss sårbarhet kommer över en viss gräns (t.ex 75%), så får man en popup som frågar om inte hackaren ska testa den sårbarheten (tillsammans med rapporterna som visar detta)

