# Description

- NOTE: This is unfinished, it is almost done but full gameplay hasn't quite been perfected
- NOTE: requires Jfoenix (https://github.com/jfoenixadmin/JFoenix) and Java FX v11+ to run. To run, open in Intellij as a project and add the JFoenix and JavaFX jars as libraries. You could try instead retrieving these dependencies through Maven if you wish however I have not tested this, although it is something I am looking to implement soon (eventually I will have a working jar file ready for the average joe to play)
- WARNING: inappropriate content. 
- Credit for the original text file for questions and answers goes to https://github.com/nodanaonlyzuul/against-humanity, I have changed the questions and answers slightly here and there. 
- Server client game similar to cards against humanity written in Java FX (note I am not affiliated with cards against humanity in any way and I am not profiting from this project at all, it is simply for fun and educational purposes. Feel free to make use of the source code however you please).

## Server Usage

- To run a server session, cd into out/production/CheekyCards and run "java Main server [number of expected players]"
- The server must be started before any client sessions try to connect. When the server session starts, it will print its global ip address which the host can forward to all the players for them to type into the "paste the link" field of the login prompt
- If you wish to play a client session on the same machine as the hosting server, simply type "localhost" in the "paste the link" field of the login prompt

## Client Usage

- Build the module, run "Main"
- You will be greeted with a login prompt asking for the address of the server, your requested username, a custom question card you want to add to the deck and a custom answer card you want to add to the deck
- If your username is already in use then the server will deny your join request and you will recieve an error dialog message prompting you to choose a different username
- Once your join request has been approved by the server the main GUI will appear and the game will start once the expected number of players have connected
