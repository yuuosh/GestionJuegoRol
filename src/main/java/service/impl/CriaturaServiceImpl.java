package service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import dao.PersonajeDao;
import dao.impl.PersonajeDaoImpl;
import dto.CriaturaDto;
import entities.Personaje;
import entities.criatura.Criatura;
import exceptions.ReglaJuegoException;
import service.CriaturaService;

public class CriaturaServiceImpl implements CriaturaService {

    private final PersonajeDao personajeDao = new PersonajeDaoImpl();
    private static final int MAX_CRIATURAS = 5;
    private CriaturaDto mapToDto(Criatura c) {
        if (c == null) return null;

        String tipo = c.getClass().getSimpleName().toUpperCase();
        Long personajeId = (c.getPersonaje() != null) ? c.getPersonaje().getId() : null;

        return new CriaturaDto(
                c.getId(),
                tipo,
                c.getNombre(),
                c.getAlias(),
                c.getNivel(),
                c.getExperiencia(),
                c.getPuntosVida(),
                c.getPuntosAtaque(),
                c.getTipoAtaque(),
                personajeId
        );
    }

    private static final Map<String, Supplier<Criatura>> CRIATURAS = Map.of(
        "GUSANO", () -> new Criatura("Gusano", null, 1, 0, 70, 2, "Disparo de seda"),
        "CONEJO", () -> new Criatura("Conejo", null, 1, 3, 50, 2, "Patada salto"),
        "MOSQUITO", () -> new Criatura("Mosquito", null, 1, 0, 10, 10, "PicaduraFatal"),
        "RATON", () -> new Criatura("Raton", null, 1, 0, 20, 1, "MordiscoInfeccioso"),
        "JABALI", () -> new Criatura("Jabali", null, 11, 35, 40, 25, "Cornada"),
        "LOBO", () -> new Criatura("Lobo", null, 1, 8, 30, 35, "Mordisco feroz"),
        "PEZ PREHISTORICO GIGANTE", () -> new Criatura("PezPrehistoricoGigante", null, 1, 9, 35, 40, "Mordisco Devastador"),
        "SILURO", () -> new Criatura("Siluro", null, 10, 0, 30, 30, "Mordisco")
    );

    private Criatura construirCriatura(String tipoCriatura) throws ReglaJuegoException {
        if (tipoCriatura == null || tipoCriatura.isBlank()) {
            throw new ReglaJuegoException("Tipo de criatura obligatorio.");
        }

        String t = tipoCriatura.trim().toUpperCase();

        Supplier<Criatura> supplier = CRIATURAS.get(t);
        if (supplier == null) {
            throw new ReglaJuegoException(
                "Tipo inválido: " + tipoCriatura +
                " (usa GUSANO / CONEJO / MOSQUITO / RATON / JABALI / LOBO / PEZ PREHISTORICO GIGANTE / SILURO)"
            );
        }

        return supplier.get();
    }

    @Override
    public List<CriaturaDto> listarPorPersonaje(Long personajeId) throws ReglaJuegoException {
        if (personajeId == null) throw new ReglaJuegoException("El personajeId es obligatorio.");

        Personaje p = personajeDao.findByIdFetchAll(personajeId);
        if (p == null) throw new ReglaJuegoException("No existe personaje con id=" + personajeId);

        List<Criatura> lista = p.getCriaturas();
        if (lista == null || lista.isEmpty()) return java.util.Collections.emptyList();

        List<CriaturaDto> res = new java.util.ArrayList<>();
        for (Criatura c : lista) res.add(mapToDto(c));
        return res;
    }
    
    @Override
    public CriaturaDto invocarCompanero(Long personajeId, String tipoCriatura, String alias) throws ReglaJuegoException {

        // 1) validar entrada
        if (personajeId == null) throw new ReglaJuegoException("El id del personaje es obligatorio.");
        if (tipoCriatura == null || tipoCriatura.trim().isEmpty()) throw new ReglaJuegoException("El tipo de criatura es obligatorio.");

        // 2) cargar personaje con criaturas (importante para contar bien y evitar Lazy)
        Personaje p = personajeDao.findByIdFetchAll(personajeId);
        if (p == null) throw new ReglaJuegoException("No existe personaje con id=" + personajeId);

        if (p.getCriaturas() == null) p.setCriaturas(new ArrayList<>());

        // 3) regla: máximo criaturas
        if (p.getCriaturas().size() >= MAX_CRIATURAS) {
            throw new ReglaJuegoException("No puedes tener más de " + MAX_CRIATURAS + " compañeros.");
        }

        // 4) construir criatura por tipo
        String tipo = tipoCriatura.trim().toUpperCase();
        Criatura nueva = construirCriatura(tipo);

        // 5) alias (si viene vacío, ponemos el nombre base)
        if (alias == null || alias.trim().isEmpty()) {
            nueva.setAlias(nueva.getNombre());
        } else {
            nueva.setAlias(alias.trim());
        }

        // 6) enlazar FK usando el método seguro
        p.addCriatura(nueva);

        // 7) persistir: con cascade, basta update(p)
        Personaje actualizado = personajeDao.update(p);

        // 8) obtener la criatura persistida para devolver DTO
        // Como acabamos de añadir una, normalmente la última es la nueva:
        List<Criatura> lista = actualizado.getCriaturas();
        Criatura persistida = lista.get(lista.size() - 1);

        return mapToDto(persistida);
    }
}

