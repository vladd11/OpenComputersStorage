OpenComputersStorage makes it possible to build big chest storages in Minecraft that supports sorting, requesting and searching needed items.
I use [OpenComputers](https://github.com/MightyPirates/OpenComputers) mod to create programmable robots (who will do all the stuff).
This project have 2 parts:
1. Android app, acts as server for robots;
2. Lua program for robot.

Android app contains [NanoHTTPd](https://github.com/NanoHttpd/nanohttpd) server for serve robots. They make reqeusts to app every loop tact to request commands. It may looks bad, but OpenComputers not support listening, so you can't make server on robots side.

Lua program have a simple commands listener and X axis pathfinder. Beause of that you should make storages like it:![image](https://user-images.githubusercontent.com/27568333/148927066-8cc69471-a2a8-483f-aae4-99eaaa307e19.png)

Steps to transfer it to your world:
- Build robot. Only chest, inventory controller and navigation upgrade needed. I also use monitor & keyboard to debug.
![image](https://user-images.githubusercontent.com/27568333/148928904-aadc7819-ec78-422a-84d8-b9223e06003d.png)
- Upload all lua files and .shrc to robot's home using wget.
- Create chests directory and add a files with chest names (any string).
- In this files you should write the chest coordinate and side (format: X Y Z SIDE)
Sides:
0. Down
1. Up
2. Back
3. Forward
4. Right
5. Left

- Start app on your Android phone.
- Power on your robot
- App should display all available items or their icons (app take it from assets folder).
OR
- Download my world from releases.

This program works really poor, it should be debugged and rewrited.
