# clj-chess-sumarry
Makes summaries of a PGN chess game by drawing a board for a given interval of moves.
Output is generated in the form of a html file and png files.
## Usage
```
$java -jar clj-chess-sumarry-0.1.0-standalone.jar interval game.pgn output-base-name
```
## Examples
```
[cgroza@HAL clj-chess-sumarry]$ java -jar target/uberjar/clj-chess-sumarry-0.1-standalone.jar 10 tests.pgn image
[cgroza@HAL clj-chess-sumarry]$ ls
image image-0.png  image-2.png  image-4.png
image-1.png  image-3.png  image-5.png
```
The file ``image`` contains the ``pgn`` text interspersed with the image files
at their corresponding positions.
### Images of ordered boards:
![Alt text](image-0.png?raw=true)

![Alt text](image-1.png?raw=true)

![Alt text](image-2.png?raw=true)

![Alt text](image-3.png?raw=true)

![Alt text](image-4.png?raw=true)

![Alt text](image-5.png?raw=true)
## License
Copyright © 2016 cgroza
GPL 3
