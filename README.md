Grupo 21
Integrantes:
Javiera Antonia Diaz Grandon 
Tomas Ignacio Pizarro Abarca
Pablo Sebastian Bascuñan Espina

### Sobre el proyecto:

Programa diseñado para facilitar la creación de torneos, en él se pueden registrar Jugadores/Equipos en torneos de eliminatoria directa, eliminatoria doble o liga simple. 
El sistema permite asignar una imagen para representar al Jugador/Equipo, se pueden seleccionar iconos ya cargados en el programa o puede agregar imágenes desde su computadora.

Una vez están todos los participantes registrados, se puede avanzar a la siguiente pestaña para generar la tabla de enfrentamientos. Dentro se puede editar la llave inicial para que el usuario decida los enfrentamientos. En caso de que en un torneo de Eliminatoria no haya suficientes participantes el programa integra el sistema de “Byes”.

En la pestaña “Calendario” se genera una lista con la hora de los partidos, El organizador podrá editar la fecha de cada Jornada.

### Para ejecutar el proyecto:
1. Clonar el repositorio.
2. Abrir el proyecto en un IDE (intelliJ IDEA recomendado).
3. Configurar el SDK (java 17 o superior).
4. Ejecutar la clase "Main" ubicada en "src/main/java/main/Main.java".

### Arquitectura y Patrones de Diseño

Este proyecto fue desarrollado asplicando una separacion estricta entre la logica y la interfaz grafica. Para lograr un codigo escalable y mantenible, se implementaron los siguientes patrones de diseño:

1. Factory Method(Creacional):Implementado en la clase "ParticipanteFactory". Se encarga de instanciar objetos concretos ("Jugador" o "Equipo") dependiendo de la seleccion del usuario en la interfaz. Esto oculta la complejidad de cracion y asegura que la vista no dependa directamente de las clases concretas, respetando el principio de responsabilidad unica.

2. Strategy(Comportamiento):Implementado a traves del enum "Disciplina". Aprovechando las capacidades avanzadas de Java, cada deporte actua como una estrategia concreta que sobrescribe metodos abstractos como "esValido()" y "formatearPuntaje()". Esto permite agregar nuevas reglas o deportes en el futuro sin modificar la logica del torneo (Principio open/closed).

3. Observer(Comportamiento):Utilizado para lograr un desacoplamiento total entre el Modelo y la Vista. La clase "GestorTorneo" actua como el Sujeto/Observable, mientras que las vistas como "VentanaRegistro" y "PanelTablaTorneo" implementan la interfaz "Observer". Cada vez que hay un cambio en las inscripciones o enfrentamientos, el gestor notifica a las vistas para que se actualicen de forma reactiva.

El diagrama UML se completo mediante las herramientas de git

## Diagrama UML de clases:

```mermaid
classDiagram
    direction TB


    %% Logica

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
        +intercambiarParticipantes(p1: Participante, p2: Participante) void
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
        -rutaAvatar: String
        +Participante(id: String, nombre: String, contacto: String)
        +getId() String
        +getNombre() String
        +getContacto() String
        +getRutaAvatar() String
        +getTipo()* String
        +setNombre(nombre: String) void
        +setContacto(contacto: String) void
        +setRutaAvatar(ruta: String) void
        +toString() String
    }

    class Jugador {
        +Jugador(id: String, nombre: String, contacto: String)
        +getTipo() String
    }

    class Equipo {
        -miembros: List~String~
        +Equipo(id: String, nombre: String, contacto: String)
        +agregarMiembro(miembro: String) void
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
        -puntaje1: float
        -puntaje2: float
        -llave: String
        -hora: String
        -recinto: String
        +Enfrentamiento(p1: Participante, p2: Participante)
        +Enfrentamiento(p1: Participante, p2: Participante, ronda: int)
        +getRonda() int
        +isJugado() boolean
        +setJugado(estado: boolean) void
        +getParticipante1() Participante
        +getParticipante2() Participante
        +getPuntaje1() float
        +getPuntaje2() float
        +registrarResultado(puntos1: float, puntos2: float) void
        +getGanador() Participante
        +getLlave() String
        +setHora(hora: String) void
        +setRecinto(recinto: String) void
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

    %% gui 

    class Main {
        +main(args: String[]) void$
    }

    class VentanaRegistro {
        +VentanaRegistro()
        +actualizar() void
    }

    class PanelTablaTorneo {
        +PanelTablaTorneo()
        +actualizar() void
        -crearFilaEnfrentamiento(enf: Enfrentamiento) JPanel
    }

    class PanelCalendario {
        +PanelCalendario()
        +actualizar() void
    }

    class DialogoRegistrarResultados {
        +DialogoRegistrarResultados(padre: Frame, enf: Enfrentamiento)
    }

    class DialogoEditarEnfrentamiento {
        +DialogoEditarEnfrentamiento(padre: Frame, enf: Enfrentamiento)
    }

    class VentanaGestionInscritos {
        +VentanaGestionInscritos(padre: JFrame)
    }


    %% relaciones


    %% Relaciones Lógica
    GestorTorneo o-- Observer : -observadores 
    GestorTorneo o-- Participante : -Inscritos 
    GestorTorneo o-- Enfrentamiento : -enfrentamientos
    GestorTorneo --> Disciplina : -disciplina
    GestorTorneo --> FormatoTorneo : -formato
    GestorTorneo --> GestorTorneo : -instanciaUnica

    Enfrentamiento --> Participante : -participante1
    Enfrentamiento --> Participante : -participante2

    Participante <|-- Jugador
    Participante <|-- Equipo
    Participante <|-- ParticipanteVacio

    ParticipanteFactory ..> Participante : crea
    ParticipanteFactory ..> Jugador : instancía
    ParticipanteFactory ..> Equipo : instancía

    %% Relaciones GUI e Implementaciones
    Observer <|.. VentanaRegistro
    Observer <|.. PanelTablaTorneo
    Observer <|.. PanelCalendario

    Main ..> VentanaRegistro : Inicia

    VentanaRegistro ..> GestorTorneo : Uso
    PanelTablaTorneo ..> GestorTorneo : Uso
    PanelCalendario ..> GestorTorneo : Uso

    VentanaRegistro ..> ParticipanteFactory : Uso
    VentanaRegistro ..> VentanaGestionInscritos : Abre
    
    PanelTablaTorneo ..> DialogoRegistrarResultados : Abre
    PanelTablaTorneo ..> DialogoEditarEnfrentamiento : Abre
