# Development diary (2021)

## 3-Feb
Starting the project today. I went over the starter code and parts of the wool manual to get the info I needed to start the project.

I comment out all the tokens in the Wool.g4 file and some tests still pass....
I don't understand the testing code.

## 4-Feb
I figured out I should have watched the videos for module 2 with the calculator example before starting the project. I mostly get the testing code now.

After going through the calculator example I figured out that when Eclipse builds the project for me the new files that get created get put into a folder called 'target' in the main project directory. That means I have to manually copy over the files to the wool.lexparse package to get anything to run.
For some reason the header in the grammar file doesn't do anything.
Now I can actually get started on the project.

Having lots of trouble figuring out the lexer rule for a proper string.

It is now 11:40 PM today.... It took me literally all day but I finally figured out how to make the grammar for the strings.
I think my original approaches way overcomplicated the issue.
I tried to do "all characters besides invalid escape characters" rather than "valid characters + valid escape characters" which led me to try and negate 2 character strings which is not possible as far as I know.
Also, for some reason I thought that strings with more than one character couldn't go in parentheses.

## 5-Feb
I finished the lexer today. No real challenges with the lexer after getting stuck on strings for no reason.
I think the badSequenceofLexemes test is broken because it only works when any other token but the first one is bad.

Time to start the parser part.

Finished typing in all of the grammar rules.
After a little reordering of the lexer rules both of the given simple test files passed.

Finished writing all the tests for the basic parser rules and they all pass without any problems.
Time to test comments.

Took me a long time to get nested comments done.
I didn't realize that the order of the tokens between | mattered.

Multi-line strings gave me a little trouble. Had to add the file line endings to the escape sequences.

## 6-Feb
Pretty much done with the project at this point. Still want to write a few more test cases of bad programs and make sure the operator precedence is correct.

It looks good now time to submit.

I wish I read the antlr book before starting this project.......

## Borrowed Test Cases from:
Matt Boros:
	commentTest.wl
	exprTest.wl
	methodArgExample.wl
	stringArgTests.wl
	stringTest.wl
