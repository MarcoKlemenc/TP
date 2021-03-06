# Trabajo pr�ctico final
## Edici�n de mapas para la navegaci�n de robots

### Descripci�n

El proyecto, realizado por nosotros, consiste en una aplicaci�n cuyo objetivo es la creaci�n y edici�n de mapas para la navegaci�n de robots. La misma cuenta con un conjunto de habitaciones y caminos (trayectorias), al igual que obst�culos, que sirven para el modelado de los mapas. 

### Pisos de demostraci�n

* **pisoEjemplo.txt:** un piso creado al azar demostrando algunas de las funcionalidades, como la posibilidad de crear varias trayectorias superpuestas, de crear trayectorias que recorran 3 habitaciones, o c�mo las trayectorias bordean obst�culos.
* **pisoSede2.txt:** un piso creado en base al segundo piso de sede 2, con las habitaciones y obst�culos correspondientes. No posee trayectorias.

### Men�

Al entrar en la aplicaci�n, se puede observar en la parte superior distintas opciones:

* **Nuevo:** crea un mapa en blanco.
* **Abrir:** abre los mapas que se encuentran en el �ltimo directorio establecido.
* **Guardar:** guarda los cambios realizados dentro del mapa preexistente. En caso de ser la primera vez que se guarda, act�a como el "Guardar como".
* **Guardar como:** guarda todo lo realizado dentro de un mapa en formato txt.
* **Exportar:** exporta la imagen del mapa actual en formato jpg.
* **Escala:** permite cambiar la escala establecida para la creaci�n del mapa. La escala debe ser del formato "n�mero espacio unidad" (ej.: 5 m).
* **Orientaci�n:** permite cambiar la orientaci�n del mapa (en grados).
* **Modo:** cada modo permite realizar ciertas modificaciones. Los mismos son: "habitaci�n", "trayectoria" y "obst�culo".

### Funciones

Dentro de las funciones que se pueden realizar dentro de esta aplicaci�n contamos con:

* **Administraci�n de habitaciones:** que cuenta con el dibujo de una grilla siendo cada una de las celdas las respectivas baldosas de dicha habitaci�n. Para crear una habitaci�n se debe mantener presionado el bot�n del mouse y arrastrar hasta conseguir el tama�o deseado. Si el rect�ngulo toma un color rojizo, quiere decir que la habitaci�n que se est� queriendo crear intersecta a una ya existente y, por lo tanto, no puede ser creada.
Adem�s, estando en modo "habitaci�n", se puede cambiar tanto el tama�o de la habitaci�n como el de sus baldosas, haciendo clic previamente en la misma para seleccionarla. Para el primer caso se debe arrastrar hasta llegar al tama�o deseado. Para el segundo se debe girar la rueda del mouse. Adem�s, estando una habitaci�n seleccionada, se mostrar�n las dimensiones de la misma de acuerdo con la escala utilizada, y puede ser eliminada haciendo clic nuevamente. Para anular la selecci�n se debe hacer clic fuera de la habitaci�n.
* **Administraci�n de obst�culos:** se puede crear un obst�culo, estando en modo "obst�culo", haciendo clic en la baldosa obstaculizada. De la misma manera, se puede eliminar cualquier obst�culo haciendo nuevamente clic en el mismo.
* **Administraci�n de trayectorias:** en modo "trayectoria" se puede crear una trayectoria haciendo clic en las baldosas que servir�n de extremos de la misma. Las trayectorias que se van armando se distinguen unas de otras mediante colores. Para "seleccionar" una trayectoria (para poder ver baldosas "tapadas" por otras trayectorias o eliminarla) se debe hacer clic en una baldosa visible correspondiente a la misma. Haciendo clic de nuevo se la eliminar�. Para cancelar una trayectoria seleccionada se debe hacer clic fuera de la misma.
* **Administraci�n de puertas:** se pueden crear puertas que comuniquen las habitaciones juntando 2 habitaciones desde cualquier lado de las mismas y haciendo clic en la intersecci�n de las mismas. Se pueden colocar tanto de forma horizontal como vertical. Adem�s se pueden cambiar de tama�o simplemente arrastrando el mouse en la puerta hasta el tama�o deseado. Para eliminarlas se debe hacer clic nuevamente.

### Detalles t�cnicos

La aplicaci�n fue realizada en Java, y se ejecuta abriendo el archivo .jar suministrado (se requiere la instalaci�n de Java Runtime Environment). Utiliza la librer�a Swing para la interfaz gr�fica y la librer�a AWT (Abstract Window Toolkit) para graficar los componentes y leer las acciones del usuario.
La aplicaci�n genera un archivo "dir.ini", que almacena el �ltimo directorio utilizado para abrir y/o guardar.

### Limitaciones de la aplicaci�n

Esta aplicaci�n, al ser un prototipo, presenta algunas limitaciones en cuanto a sus usos y opciones:

* El tama�o del piso no puede ser mayor al de la pantalla.
* Pueden surgir problemas al querer acercar una habitaci�n con otra desde la izquierda o arriba. Esto se puede ver cuando la habitaci�n "salta" para anexarse a una de las esquinas de la otra.
* Las trayectorias pueden recorrer m�s de una habitaci�n, pero no m�s de 3.
* Las trayectorias no siempre toman el mejor camino, y en algunos casos a la aplicaci�n le es imposible encontrar uno.