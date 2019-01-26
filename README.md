# Connect4
----
This application is a multiplayer Connect-Four game, otherwise known as “Four-in-a-Row,” built with Java.

## Description
----

**Importance**
Demostrates the power and flexibilty of Java socket servers

**Functions:**
1. Allows unlimited pairs of clients to play a game of connect-four over the same server.
1. Simulates a GUI of a connect-four board
1. Generates an operations file and a driver.
1. Receives user input as integer corresonding to board column number

The application includes four files: a server program, a client program, and two images for the red and yellow chips.
The two images are used to represent the two opponents in each pair of clients. 

### The Connect Four server manages roughly five tasks:
1.	Establishes connections for any number of clients and pairs opponents
2.	Simulates the game board logic via array
3.	When a player moves, board is checked for end conditions
  a.	win, loss, and tie
4.	Initializes I/O stream
5.	Sends (and receives) text messages to(from) clients

### The Connect Four client manages four tasks:
1.	Establishes a connection to server socket via matching port number
2.	Creates the graphical use interface for game board
  a.	The game window, grid, and user icons are initialized
3.	Reads mouse input and converts to game data
4.	Handles reply text messages to and from server

## Getting Started
----
These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites
----
What things you need to install the software and how to install them

```
Give examples
```

### Installation
----
Tell other users how to install your project locally. Optionally, include a gif to make the process even more clear for other people.
A step by step series of examples that tell you how to get a development env running

Say what the step will be

```
Give the example
```

And repeat

```
until finished
```

End with an example of getting some data out of the system or using it for a little demo

## Usage
----

Essentially, the server handles all the math, logic, and threads of the game, and the client handles what the user sees and inputs in the game. The application allows the server to run continually, accepting an unlimited number of clients, pairing up each consecutive user. Each pair of players is assigned a red or yellow chip to mark them and set as opponents. Taking turns, each player fills a cell with his respective chip via a mouse click. One player wins when he gets four chips in a row in any direction. A tie occurs when the board array is full without any winners. After a win or tie, each player is prompted to play again.

## Roadmap
----
1. Deploy server on live
1. Handle player quit: informing other client of quit and prompting for 'new match' or 'quit'

## Support
----
1. Open a request on this repository
1. Email me at nsean.robinson@gmail.com

## Authors
----
- **Nathan Robinson** - *Initial work* -

## License
----
This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

	


