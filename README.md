# Evolving Airplanes
Java App to simulate evolving bots that use neural networks to fly a plane, can attack other planes and dodge attacks from them.
Each plane has an artificial neural network that evolves using evolutionary algorithms.

Airplanes can fire a limited amount of missiles, each missile can be fired without aiming or waiting a moment to lock onto the target
If fired without lock on the missile just goes straight until it loses its fuel.
If fired on target the missile will chase the target until it loses its fuel,
for these situations the planes can deploy flares that are also limited to avoid missiles.

Both the missiles and the flares are limited but they recharge from time to time, the objective is to make the planes
evolve to know how to fire only when on target and use flares only when a missile is chasing them, 
then for the more advanced ones they are expected to know how to dodge the missiles with maneuvers after running out
flares.

# Evolucionando Aviones
App Java para simular la evolucion de bot que usan redes neuronales para pilotar un avion, atacar a otros aviones y esquivar ataques de otros aviones.
Cada avión cuenta con una red neuronal artificial que evoluciona usando algoritmos evolutivos.

Los aviones pueden disparar una cantidad limitada de misiles, cada misil puede ser disparado sin apuntar o esperar un momento para fijar el objetivo
Si se dispara sin fijar el objetivo el misil simplemente va recto hasta que pierde su combustible.
Si se dispara fijando el objetivo el misil va a perseguir al objetivo hasta que pierda su combustible, 
para éstas situaciones los aviones pueden desplegar bengalas que también son limitadas para evitar misiles.

Tanto los misiles cómo las bengalas son limitadas pero se recargan cada cierto tiempo, el objetivo es hacer que los aviones
evolucionen para saber disparar sólo cuando tienen el objetivo fijado y usar las bengalas sólo cuando un misil los esté
persiguiendo, luego para los más avanzados se espera que sepan esquivar los misiles con maniobras después de quedarse sin 
bengalas.

## RUN
Run Java file from fuentes/vista/Menu.java