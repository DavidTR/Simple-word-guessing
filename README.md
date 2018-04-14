# Simple-word-guessing

Simple word guessing game implemented in Java based in a multithreaded server.

## Game rules

The player begins with an initial score of 500 points.

The player communicates with the server using commands:

* PRINT: Shows the clues that the user has requested. The clues are letters that are sequentially placed from the beginning of the word. The rest of the word is hidden with underscores.
* CLUE: Gives the user a clue in form of the next letter of the word. The user score is reduced by 50.
* GUESS: The player the opportunity to guess the hidden word and win the game!, but if he misses, his score is reduced by 100. If the user wins, the connection with the server closes.
* END: Finishes the game without winning. The score is printed and the connection with the server gets closed.

There's an optional feature (not available but implemmented) that allows the game data to be saved in a remote FTP server.
