# Trabajo práctico final
## Edición de mapas para la navegación de robots

### Descripción

El proyecto, realizado por nosotros, consiste en una aplicación cuyo objetivo es la creación y edición de mapas para la navegación de robots. La misma cuenta con un conjunto de habitaciones y caminos (trayectorias), al igual que obstáculos, que sirven para el modelado de los mapas. 

### Menú

Al entrar en la aplicación, se puede observar en la parte superior distintas opciones:

* **Nuevo:** crea un mapa en blanco.
* **Abrir:** abre los mapas que se encuentran en el último directorio establecido.
* **Guardar:** guarda los cambios realizados dentro del mapa preexistente. En caso de ser la primera vez que se guarda, actúa como el "Guardar como".
* **Guardar como:** guarda todo lo realizado dentro de un mapa en formato txt.
* **Exportar:** exporta la imagen del mapa actual en formato jpg.
* **Escala:** permite cambiar la escala establecida para la creación del mapa.
* **Orientación:** permite cambiar la orientación del mapa (en grados).
* **Modo:** cada modo permite realizar ciertas modificaciones. Los mismos son: "habitación", "trayectoria" y "obstáculo".

### Funciones

Dentro de las funciones que se pueden realizar dentro de esta aplicación contamos con:

* **Administración de habitaciones:** que cuenta con el dibujo de una grilla siendo cada una de las celdas las respectivas baldosas de dicha habitacion. Para crear una habitación se debe mantener presionado el botón del mouse y arrastrar hasta conseguir el tamaño deseado. Si el rectángulo toma un color rojizo, quiere decir que la habitación que se está queriendo crear intersecta a una ya existente y, por lo tanto, no puede ser creada.
Además, estando en modo "habitación", se puede cambiar tanto el tamaño de la habitación como el de sus baldosas, haciendo clic previamente en la misma para seleccionarla. Para el primer caso se debe arrastrar hasta llegar al tamaño deseado. Para el segundo se debe girar la rueda del mouse.
Además, estando una habitación seleccionada, la misma se puede eliminar haciendo clic nuevamente dentro de la misma. Para anular la selección se debe hacer clic fuera de la habitación.
* **Administración de obstaculos:** se puede crear un obstáculo, estando en modo "obstáculo", haciendo clic en la baldosa obstaculizada. De la misma manera, se puede eliminar cualquier obstáculo haciendo nuevamente clic en el mismo.
* **Administración de trayectorias:** en modo "trayectoria" se puede crear una trayectoria haciendo clic en las baldosas que servirán de extremos de la misma. Para eliminarla, se debe hacer clic en cualquier baldosa correspondiente a la misma. Las trayectorias que se van armando se distinguen unas de otras a través de colores.
* **Administración de puertas:** se pueden crear puertas que comuniquen las habitaciones juntando 2 habitaciones desde cualquier lado de las mismas y haciendo clic en la intersección de las mismas. Se pueden colocar tanto de forma horizontal como vertical. Ademas, se pueden cambiar de tamaño simplemente arrastrando el mouse en la puerta hasta el tamaño deseado. Para eliminarlas se debe hacer clic nuevamente en las mismas.

### Detalles técnicos

La aplicación fue realizada en Java, y se ejecuta abriendo el archivo .jar suministrado (se requiere la instalación de Java Runtime Environment). Utiliza la librería Swing para la interfaz gráfica y la librería AWT (Abstract Window Toolkit) para graficar los componentes y leer las acciones del usuario.

### Limitaciones de la aplicación

Esta aplicación, al ser un prototipo, presenta algunas limitaciones en cuanto a sus usos y opciones:

* El tamaño del piso no puede ser mayor al de la pantalla.
* Pueden surgir problemas al querer acercar una habitación con otra desde la izquierda o arriba. Esto se puede ver cuando la habitación "salta" para anexarse a una de las esquinas de la otra.
* Las trayectorias no siempre toman el mejor camino o un buen camino. Puede ser que utilice más de una baldosa cerca de las puertas, que pase por encima de algun obstáculo o que directamente no pueda encontrar una trayectoria. Además, las trayectorias es que no se guardan en una estructura de grafo, por lo que tampoco se puede aplicar ningun algoritmo de optimización sobre las mismas.