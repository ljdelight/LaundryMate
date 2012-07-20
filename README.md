# LaundryMate
This is the GUI code of a small project to send an email when a monitored current transitions from ON to OFF. 

We have a pair of TI eZ430-RF2500 2.4GHz wireless microcontrollers: one is monitoring a current sensor, and the other is connected to a PC's UART. When the current monitoring MCU sees a change in current, it sends a message to the other device connected to the PC. Then the PC-connected MCU communicates to the GUI about the event. From there, the GUI may send out an email. 

## Create Jar and Run
To build the jar: Navigave to the root directory and call Ant; the default build is to create the distibution. Or, 
```$ ant compile```.

To run the program: Call Ant with 'run'. ```$ant run```. The Texas Instrument eZ430 drivers are required to comminucate to the eZ430 via the UART.
