Hola Roberto, hemos hecho lo que hemos podido, hemos realizado el envio/escucha de keepalive, la asignación de 
id por parte del master y también el consenso para seleccionar el nuevo master cuando el actual se cae. Esto se 
puede ver ejecutando la clase controller.java, ya que no nos ha dado tiempo a implementarlo en la ventana. También hemos
hecho los métodos que realizan consultas a la base de datos, pero no hemos hecho la sincronización de la base de datos, esto
queda pendiente para la siguiente entrega. 

En la clase controller estamos iniciando 4 trackers distintos con sus listeners y senders de mensajes. Al principio salen 
todos los listeners en la consola ya que los inicializamos a la vez. Poco a poco se van a ir cargando y asignando ids. Después
de que se asignen correctamente las ids comienzan los hilos para realizar las comprobaciones de si algun tracker se ha caido o no.
Estamos parando un tracker que no es master primero, y después paramos el master, con 5 segundos de diferencia. También hemos probado
esto mismo a la inversa y sigue funcionando. Para elegir el nuevo master el criterio implementado es el siguiente tracker activo
con la menor id de toda la lista de trackers. Una vez se pare el master, dale unos 10 - 15 segundos para que se finalice la 
asignación del nuevo master. 

Nos gustaría tener una tutoría contigo, lo más pronto posible, para aclarar las incidencias y las dudas que nos han surgido.
