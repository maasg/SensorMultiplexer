# Sensor Multiplexer

This sensor multiplexer is an akka-based application that creates a stream of data consisting of 
one physical device and n simulated devices.
The data used for the simulated devices is a few hours worth of data from the actual sensor. 
Simulated devices will take a segment of data and replay it over time. The data segments are replayed first 
in order and then in reverse order, repeating that pattern. This ensures that data has no weird 'steps' up or down that 
could be mistaken for a surge.

Usage: `sbt run <kafka broker> <kafka topic>`

This project is meant to be used with a physical controller, like this one:
![Arduino-compatible Microview + DTH11 tem+humidity sensor](./docs/sensor.jpg)

The data reported is a comma-separated tuple.

