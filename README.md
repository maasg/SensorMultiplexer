# Sensor Multiplexer

This sensor multiplexer is an akka-based application that creates a stream of data consisting of 
one physical device and n simulated devices.
The data used for the simulated devices is a few hours worth of data from an actual sensor. 

Simulated devices will take a segment of data and replay it over time. The data segments are replayed first 
in order and then in reverse order, repeating that pattern. This ensures that data has no weird 'steps' up or down that 
could be mistaken for a surge.

Usage: `sbt run <kafka broker> <kafka topic> [--use-sensor]`

When the `--use-sensor` flag is used, the program expects a physical sensor connected
For demonstration purposed, we have used:
- a temperature-humidity sensor, like this one:
  [Arduino-compatible Microview + DTH11 tem+humidity sensor](./docs/IMG_20171127_144744158.jpg)
- [a manual-dial sensor display](./docs/manual-controller.jpg)
- [a sound-level sensor](./docs/IMG_20181114_135632.jpg) 

The data reported to Kafka is a JSON object consisting of:
- id : the id of the sensor (simulated sensors start with 'sim-')
- a current timestamp. For the simulated sensors, the timestamp in the data is replaced by the current time
- the sensor value

