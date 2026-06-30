Integrantes:
Javiera Antonia Diaz Grandon 
Tomas Ignacio Pizarro Abarca
Pablo Sebastian Bascuñan Espina

### Para ejecutar el proyecto:
1. Clonar el repositorio.
2. Abrir el proyecto en un IDE (intelliJ IDEA recomendado).
3. Configurar el SDK (java 17 o superior).
4. Ejecutar la clase "Main" ubicada en "src/main/java/org/Main.java".creo que esto hay que cambiarlo

##Arquitectura y Patrones de Diseño

Este proyecto fue desarrollado asplicando una separacion estricta entre la logica de negocio y la interfaz grafica. Para lograr un codigo escalable y mantenible, se implementaron los siguientes patrones de diseño:

1. Factory Method(Creacional):** Implementado en la clase `ParticipanteFactory`. Se encarga de instanciar objetos concretos (`Jugador` o `Equipo`) dependiendo de la seleccion del usuario en la interfaz. Esto oculta la complejidad de cracion y asegura que la vista no dependa directamente de las clases concretas, respetando el principio de responsabilidad unica.

2. Strategy(Comportamiento):** Implementado a traves del enum `Disciplina`. Aprovechando las capacidades avanzadas de Java, cada deporte actua como una estrategia concreta que sobrescribe metodos abstractos como `esValido()` y `formatearPuntaje()`. Esto permite agregar nuevas reglas o deportes en el futuro sin modificar la logica del torneo (Principio open/closed).

3. Observer(Comportamiento):** Utilizado para lograr un desacoplamiento total entre el Modelo y la Vista. La clase `GestorTorneo` actúa como el *Sujeto/Observable*, mientras que las vistas como `VentanaRegistro` y `PanelTablaTorneo` implementan la interfaz `Observer`. Cada vez que hay un cambio en las inscripciones o enfrentamientos, el gestor notifica a las vistas para que se actualicen de forma reactiva.

El **diagrama UML** se completo mediante las herramientas de git

## Diagrama UML de clases:

```mermaid
classDiagram
    %% Patrón Observer
    class Observer {
        <<interface>>
        +actualizar()
    }
    class GestorTorneo {
        <<Singleton / Observable>>
        -instanciaUnica: GestorTorneo
        -observadores: List~Observer~
        +getInstancia() GestorTorneo
        +registrarObserver(o: Observer)
        +notificar()
    }
    class VentanaRegistro {
        +actualizar()
    }
    class PanelTablaTorneo {
        +actualizar()
    }
    GestorTorneo o-- Observer : notifica
    Observer <|.. VentanaRegistro
    Observer <|.. PanelTablaTorneo

    %% Patrón Factory y Null Object
    class Participante {
        <<abstract>>
        -id: String
        -nombre: String
        -contacto: String
        +getTipo()* String
    }
    class Jugador {
        +getTipo() String
    }
    class Equipo {
        -nombresMiembros: List~String~
        +getTipo() String
    }
    class ParticipanteVacio {
        +getTipo() String
    }
    Participante <|-- Jugador
    Participante <|-- Equipo
    Participante <|-- ParticipanteVacio : Null Object

    class ParticipanteFactory {
        +crearParticipante(tipo, id, nombre, contacto) Participante
    }
    ParticipanteFactory ..> Participante : instancia
    %% Patrón Strategy
    class Disciplina {
        <<enumeration / Strategy>>
        FUTBOL
        AJEDREZ
        VIDEOJUEGOS
        +esValido(p1, p2)* boolean
        +formatearPuntaje(p1, p2)* String
    }
