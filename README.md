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

1. Factory Method(Creacional):Implementado en la clase "ParticipanteFactory". Se encarga de instanciar objetos concretos ("Jugador" o "Equipo") dependiendo de la seleccion del usuario en la interfaz. Esto oculta la complejidad de cracion y asegura que la vista no dependa directamente de las clases concretas, respetando el principio de responsabilidad unica.

2. Strategy(Comportamiento):Implementado a traves del enum "Disciplina". Aprovechando las capacidades avanzadas de Java, cada deporte actua como una estrategia concreta que sobrescribe metodos abstractos como "esValido()" y "formatearPuntaje()". Esto permite agregar nuevas reglas o deportes en el futuro sin modificar la logica del torneo (Principio open/closed).

3. Observer(Comportamiento):Utilizado para lograr un desacoplamiento total entre el Modelo y la Vista. La clase "GestorTorneo" actua como el Sujeto/Observable, mientras que las vistas como "VentanaRegistro" y "PanelTablaTorneo" implementan la interfaz "Observer". Cada vez que hay un cambio en las inscripciones o enfrentamientos, el gestor notifica a las vistas para que se actualicen de forma reactiva.

El diagrama UML se completo mediante las herramientas de git

## Diagrama UML de clases:

```mermaid
classDiagram
    direction TB

    class Observer {
        <<interface>>
        +actualizar() void
    }

    class GestorTorneo {
        -instanciaUnica: GestorTorneo$
        -nombre: String
        -disciplina: Disciplina
        -formato: FormatoTorneo
        -Inscritos: List~Participante~
        -enfrentamientos: List~Enfrentamiento~
        -observadores: List~Observer~
        -rondaActual: int
        -GestorTorneo()
        +getInstancia() GestorTorneo$
        +registrarObserver(o: Observer) void
        +eliminarObserver(o: Observer) void
        +notificar() void
        +configurarTorneo(nombre: String, disciplina: Disciplina, formato: FormatoTorneo) void
        +inscribirParticipante(participante: Participante) void
        +eliminarParticipante(participante: Participante) void
        +generarTorneo() void
        +rondaActualTerminada() boolean
        +avanzarRonda() void
        +getNombre() String
        +getDisciplina() Disciplina
        +getFormato() FormatoTorneo
        +getInscritos() List~Participante~
        +getEnfrentamientos() List~Enfrentamiento~
    }

    class Participante {
        <<abstract>>
        -id: String
        -nombre: String
        -contacto: String
        +Participante(id: String, nombre: String, contacto: String)
        +getId() String
        +getNombre() String
        +getContacto() String
        +getTipo()* String
        +setNombre(nombre: String) void
        +setContacto(contacto: String) void
        +toString() String
    }

    class Jugador {
        +Jugador(id: String, nombre: String, contacto: String)
        +getTipo() String
    }

    class Equipo {
        +Equipo(id: String, nombre: String, contacto: String)
        +getTipo() String
    }

    class ParticipanteVacio {
        +ParticipanteVacio()
        +getTipo() String
    }

    class ParticipanteFactory {
        +crearParticipante(tipo: String, id: String, nombre: String, contacto: String) Participante$
    }

    class Enfrentamiento {
        -participante1: Participante
        -participante2: Participante
        -ronda: int
        -jugado: boolean
        +Enfrentamiento(p1: Participante, p2: Participante)
        +Enfrentamiento(p1: Participante, p2: Participante, ronda: int)
        +getRonda() int
        +isJugado() boolean
        +getParticipante1() Participante
        +getParticipante2() Participante
        +getGanador() Participante
    }

    class Disciplina {
        <<enumeration>>
        FUTBOL
        AJEDREZ
        VIDEOJUEGOS
        BOXEO
        TENIS
        VOLEIBOL
        BASQUETBOL
        +esValido(p1: float, p2: float) boolean
    }

    class FormatoTorneo {
        <<enumeration>>
        LIGA_SIMPLE
        ELIMINATORIA_DIRECTA
        ELIMINATORIA_DOBLE
    }

    %% Relaciones de Asociación y Composición
    GestorTorneo o-- Observer : -observadores (0..*)
    GestorTorneo o-- Participante : -Inscritos (0..*)
    GestorTorneo o-- Enfrentamiento : -enfrentamientos (0..*)
    GestorTorneo --> Disciplina : -disciplina
    GestorTorneo --> FormatoTorneo : -formato
    GestorTorneo --> GestorTorneo : -instanciaUnica

    Enfrentamiento --> Participante : -participante1
    Enfrentamiento --> Participante : -participante2

    %% Relaciones de Herencia e Implementación
    Participante <|-- Jugador
    Participante <|-- Equipo
    Participante <|-- ParticipanteVacio

    %% Relaciones de Dependencia (Uso)
    ParticipanteFactory ..> Participante : crea
    ParticipanteFactory ..> Jugador : instancía
    ParticipanteFactory ..> Equipo : instancía
