Hola Roberto, hemos hecho lo que hemos podido, hemos realizado el envio/escucha de keepalive, la asignaci�n de 
id por parte del master y tambi�n el consenso para seleccionar el nuevo master cuando el actual se cae. Esto se 
puede ver ejecutando la clase controller.java, ya que no nos ha dado tiempo a implementarlo en la ventana. Tambi�n hemos
hecho los m�todos que realizan consultas a la base de datos, pero no hemos hecho la sincronizaci�n de la base de datos, esto
queda pendiente para la siguiente entrega. 

En la clase controller estamos iniciando 4 trackers distintos con sus listeners y senders de mensajes. Al principio salen 
todos los listeners en la consola ya que los inicializamos a la vez. Poco a poco se van a ir cargando y asignando ids. Despu�s
de que se asignen correctamente las ids comienzan los hilos para realizar las comprobaciones de si algun tracker se ha caido o no.
Estamos parando un tracker que no es master primero, y despu�s paramos el master, con 5 segundos de diferencia. Tambi�n hemos probado
esto mismo a la inversa y sigue funcionando. Para elegir el nuevo master el criterio implementado es el siguiente tracker activo
con la menor id de toda la lista de trackers. Una vez se pare el master, dale unos 10 - 15 segundos para que se finalice la 
asignaci�n del nuevo master. 

Nos gustar�a tener una tutor�a contigo, lo m�s pronto posible, para aclarar las incidencias y las dudas que nos han surgido.
