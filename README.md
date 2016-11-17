# Trabajo pr�ctico final
## Edici�n de mapas para la navegaci�n de robots

### Descripci�n

El proyecto, realizado por nosotros, consiste en una aplicaci�n cuyo objetivo es la creaci�n y edici�n de mapas para la navegaci�n de robots. La misma cuenta con un conjunto de habitaciones y caminos (trayectorias), al igual que obst�culos, que sirven para el modelado de los mapas. 

### Men�

Al entrar en la aplicaci�n, se puede observar en la parte superior distintas opciones:

* **Nuevo:** crea un mapa en blanco.
* **Abrir:** abre los mapas que se encuentran en el �ltimo directorio establecido.
* **Guardar:** guarda los cambios realizados dentro del mapa preexistente. En caso de ser la primera vez que se guarda, act�a como el "Guardar como".
* **Guardar como:** guarda todo lo realizado dentro de un mapa en formato txt.
* **Exportar:** exporta la imagen del mapa actual en formato jpg.
* **Escala:** permite cambiar la escala establecida para la creaci�n del mapa.
* **Orientaci�n:** permite cambiar la orientaci�n del mapa (en grados).
* **Modo:** cada modo permite realizar ciertas modificaciones. Los mismos son: "habitaci�n", "trayectoria" y "obst�culo".

### Funciones

Dentro de las funciones que se pueden realizar dentro de esta aplicaci�n contamos con:

* **Administraci�n de habitaciones:** que cuenta con el dibujo de una grilla siendo cada una de las celdas las respectivas baldosas de dicha habitacion. Para crear una habitaci�n se debe mantener presionado el bot�n del mouse y arrastrar hasta conseguir el tama�o deseado. Si el rect�ngulo toma un color rojizo, quiere decir que la habitaci�n que se est� queriendo crear intersecta a una ya existente y, por lo tanto, no puede ser creada.
Adem�s, estando en modo "habitaci�n", se puede cambiar tanto el tama�o de la habitaci�n como el de sus baldosas, haciendo clic previamente en la misma para seleccionarla. Para el primer caso se debe arrastrar hasta llegar al tama�o deseado. Para el segundo se debe girar la rueda del mouse.
Adem�s, estando una habitaci�n seleccionada, la misma se puede eliminar haciendo clic nuevamente dentro de la misma. Para anular la selecci�n se debe hacer clic fuera de la habitaci�n.
* **Administraci�n de obstaculos:** se puede crear un obst�culo, estando en modo "obst�culo", haciendo clic en la baldosa obstaculizada. De la misma manera, se puede eliminar cualquier obst�culo haciendo nuevamente clic en el mismo.
* **Administraci�n de trayectorias:** en modo "trayectoria" se puede crear una trayectoria haciendo clic en las baldosas que servir�n de extremos de la misma. Para eliminarla, se debe hacer clic en cualquier baldosa correspondiente a la misma. Las trayectorias que se van armando se distinguen unas de otras a trav�s de colores.
* **Administraci�n de puertas:** se pueden crear puertas que comuniquen las habitaciones juntando 2 habitaciones desde cualquier lado de las mismas y haciendo clic en la intersecci�n de las mismas. Se pueden colocar tanto de forma horizontal como vertical. Ademas, se pueden cambiar de tama�o simplemente arrastrando el mouse en la puerta hasta el tama�o deseado. Para eliminarlas se debe hacer clic nuevamente en las mismas.

### Detalles t�cnicos

La aplicaci�n fue realizada en Java, y se ejecuta abriendo el archivo .jar suministrado (se requiere la instalaci�n de Java Runtime Environment). Utiliza la librer�a Swing para la interfaz gr�fica y la librer�a AWT (Abstract Window Toolkit) para graficar los componentes y leer las acciones del usuario.

### Limitaciones de la aplicaci�n

Esta aplicaci�n, al ser un prototipo, presenta algunas limitaciones en cuanto a sus usos y opciones:

* El tama�o del piso no puede ser mayor al de la pantalla.
* Pueden surgir problemas al querer acercar una habitaci�n con otra desde la izquierda o arriba. Esto se puede ver cuando la habitaci�n "salta" para anexarse a una de las esquinas de la otra.
* Las trayectorias no siempre toman el mejor camino o un buen camino. Puede ser que utilice m�s de una baldosa cerca de las puertas, que pase por encima de algun obst�culo o que directamente no pueda encontrar una trayectoria. Adem�s, las trayectorias es que no se guardan en una estructura de grafo, por lo que tampoco se puede aplicar ningun algoritmo de optimizaci�n sobre las mismas.