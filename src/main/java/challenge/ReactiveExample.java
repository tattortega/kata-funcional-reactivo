package challenge;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public class ReactiveExample {

    public static final int VALOR_PERMITIDO = 15;
    private  Flux<Estudiante> estudianteList;

    public ReactiveExample() {
        //TODO: convertir los estudiantes a un Flux

       estudianteList = Flux.just(
                new Estudiante("raul", 30, List.of(1, 2, 1, 4, 5)),
                new Estudiante("andres", 35, List.of(4, 2, 4, 3, 5)),
                new Estudiante("juan", 75, List.of(3, 2, 4, 5, 5)),
                new Estudiante("pedro", 80, List.of(5, 5, 4, 5, 5)),
                new Estudiante("santiago", 40, List.of(4, 5, 4, 5, 5))
        );

    }

    //TODO: suma de puntajes
    public Mono<Integer> sumaDePuntajes() {
        return estudianteList.map(mapeoDeEstudianteAPuntaje())
                .reduce(0, Integer::sum);
    }

    private Function<Estudiante, Integer> mapeoDeEstudianteAPuntaje() {
        return Estudiante::getPuntaje;
    }

    //TODO: mayor puntaje de estudiante
    public Mono<Estudiante> mayorPuntajeDeEstudiante() {
        return estudianteList.sort(Comparator.reverseOrder())
                .elementAt(0);

    }

    //TODO: total de asisntencias de estudiantes con mayor puntaje basado en un  valor
    public Mono<Integer> totalDeAsistenciasDeEstudiantesConMayorPuntajeDe(int valor) {
        return estudianteList.filter(estudianteConPuntajeMayorDe(valor))
                .flatMapIterable(Estudiante::getAsistencias)
                .reduce(Integer::sum);
    }

    private Predicate<Estudiante> estudianteConPuntajeMayorDe(int valor) {
        return estudiante -> estudiante.getPuntaje() >= valor;
    }

    //TODO: el estudiante tiene asistencias correctas
    public Mono<Boolean> elEstudianteTieneAsistenciasCorrectas(Estudiante estudiante) {
        return Mono.just(estudiante)
                .filter(estudianteConAsistenciasMayorAPermitidas())
                .hasElement();
    }

    private Predicate<Estudiante> estudianteConAsistenciasMayorAPermitidas() {
        return estudiante -> estudiante.getAsistencias()
                .stream()
                .reduce(0, Integer::sum) >= VALOR_PERMITIDO;
    }

    //TODO: promedio de puntajes por estudiantes
    public Mono<Double> promedioDePuntajesPorEstudiantes() {
        return estudianteList.map(Estudiante::getPuntaje)
                .collect(Collectors.averagingDouble(puntaje -> puntaje));
    }


    //TODO: los nombres de estudiante con puntaje mayor a un valor
    public Flux<String> losNombresDeEstudianteConPuntajeMayorA(int valor) {
        return estudianteList.filter(estudianteConPuntajeMayorDe(valor))
                .map(Estudiante::getNombre);
    }



    //TODO: estudiantes aprobados
    public Flux<String> estudiantesAprobados(){
        return estudianteList.map(this::aprobarEstudiante)
                .filter(Estudiante::isAprobado)
                .map(Estudiante::getNombre);
    }

    private Estudiante aprobarEstudiante(Estudiante estudiante) {
        return Optional.of(estudiante)
                .filter(estudianteConPuntajeMayorDe(75))
                .map(this::aprobar)
                .orElse(estudiante);
    }

    public Estudiante aprobar(Estudiante estudiante) {
        var est = new Estudiante(estudiante.getNombre(), estudiante.getPuntaje(), estudiante.getAsistencias());
        est.setAprobado(true);

        return est;
    }
}
