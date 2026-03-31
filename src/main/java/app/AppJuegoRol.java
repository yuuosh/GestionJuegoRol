package app;

import java.util.List;

import dto.EquipamientoDto;
import dto.UsuarioDto;
import entities.Personaje;
import entities.criatura.Criatura;
import entities.equipo.Equipamiento;
import entities.equipo.objetos.Baya;
import entities.equipo.objetos.Cuerda;
import entities.equipo.objetos.HojaParaLimpiar;
import entities.equipo.objetos.MojonSeco;
import entities.equipo.objetos.Palo;
import entities.equipo.objetos.Piedra;
import exceptions.ReglaJuegoException;
import service.CriaturaService;
import service.EpisodioService;
import service.EquipamientoService;
import service.PersonajeService;
import service.UsuarioService;
import service.impl.CriaturaServiceImpl;
import service.impl.EpisodioServiceImpl;
import service.impl.EquipamientoServiceImpl;
import service.impl.PersonajeServiceImpl;
import service.impl.UsuarioServiceImpl;
import utilidades.HibernateUtil;
import utilidades.Utils;

public class AppJuegoRol {

    public static void main(String[] args) {
    	
//    	En Oracle-xe
//    	DROP TABLE TB_EQUIPAMIENTO CASCADE CONSTRAINTS;
//    	DROP TABLE TB_CRIATURA     CASCADE CONSTRAINTS;
//    	DROP TABLE TB_PERSONAJE    CASCADE CONSTRAINTS;
//    	DROP TABLE TB_USUARIO      CASCADE CONSTRAINTS;
//    	
    	//PRUEBA DE LA APP:
//    	1. Login y elige personaje
//
//    	2. JUGAR EPISODIO ACTUAL
//
//    	3. Sal del programa
//
//    	4. Vuelve a entrar, login, selecciona el mismo personaje
//
//    	5. 12) JUGAR EPISODIO ACTUAL → debe entrar directamente en episodio 2
//
//    	6. 10) RECARGAR PERSONAJE para comprobar inventario/criaturas guardadas

        HibernateUtil.crearConexion();

        UsuarioService usuarioService = new UsuarioServiceImpl();
        PersonajeService personajeService = new PersonajeServiceImpl();
        EquipamientoService equipamientoService = new EquipamientoServiceImpl();
        CriaturaService criaturaService = new CriaturaServiceImpl();
        EpisodioService episodioService = new EpisodioServiceImpl();

        boolean salir = false;
        UsuarioDto usuarioLogueado = null;
        Personaje personajeCreado = null;

        while (!salir) {
        	mostrarMenu(usuarioLogueado);
        	int op = Utils.pideDatoNumerico("Opcion: ");

            try {
                switch (op) {

                    case 1: {
                        String u = Utils.pideDatoCadena("Username: ");
                        String e = Utils.pideDatoCadena("Email: ");
                        String p = Utils.pideDatoCadena("Password: ");
                        String r = Utils.pideDatoCadena("Rol (JUGADOR / ADMINISTRADOR): ");

                        UsuarioDto registrado = usuarioService.registrar(u, e, p, r);
                        System.out.println("Usuario registrado OK -> " + registrado);
                        break;
                    }

                    case 2: {
                        String ul = Utils.pideDatoCadena("Username: ");
                        String pl = Utils.pideDatoCadena("Password: ");

                        usuarioLogueado = usuarioService.login(ul, pl);
                        System.out.println("ID usuario logueado: " + usuarioLogueado.getId());
                        System.out.println("Usuario logueado OK -> " + usuarioLogueado);

                        List<Personaje> personajes = personajeService.listarPorUsuario(usuarioLogueado.getId());

                        if (personajes.isEmpty()) {
                            personajeCreado = null;
                            System.out.println("No tienes personajes todavía. Crea uno con opción 4.");
                            break;
                        }

                        System.out.println("Elige personaje:");
                        for (int i = 0; i < personajes.size(); i++) {
                            System.out.println((i + 1) + ") " + personajes.get(i).getNombre()
                                    + " [" + personajes.get(i).getRazaTipo() + "]"
                                    + " (id=" + personajes.get(i).getId() + ")");
                        }

                        int idx = Utils.pideDatoNumerico("Opción: ") - 1;
                        if (idx < 0 || idx >= personajes.size()) {
                            personajeCreado = null;
                            System.out.println("Opción inválida.");
                            break;
                        }

                        personajeCreado = personajes.get(idx);
                        System.out.println("Personaje activo: " + personajeCreado);
                        break;
                    }

                    case 3: {
                    	salir = true;
                    	break;
                    }
                    case 4: {
                    	if (usuarioLogueado == null) {
                    		System.out.println("Para crear un personaje debes de hacer login primero");
                    		break;
                    	}
                    	
                    	String name = Utils.pideDatoCadena("Nombre de personaje: ");
                    	String raza = Utils.pideDatoCadena("Elige raza (MONGOL, RAPA NUI, TROGLODITA): ");
                    	
                    	personajeCreado = personajeService.crearYGuardar(usuarioLogueado.getId(), name, raza);
                    	System.out.println("Personaje creado OK -> " + personajeCreado);
                    	break;
                    }

                    case 5: {
                    	if (usuarioLogueado == null) {
                    		System.out.println("Debes de hacer login primero.");
                    		break;
                    	}
                    	
                    	if(personajeCreado == null || personajeCreado.getId() == null) {
                    		System.out.println("Debes seleccionar/crear un personaje antes de jugar.");
                    		break;
                    	}
                    	
                    	personajeCreado = ErrorHandler.handleWithReturn(() ->
                            episodioService.jugarEpisodioActual(personajeCreado.getId())
                        );

                        if (personajeCreado != null) {
                            System.out.println("Episodio terminado. Proceso guardado. Episodio actual: "
                                + personajeCreado.getEpisodioActual());
                        }

                        break;
                    }


                    case 6: {
                        if (usuarioLogueado == null) {
                            System.out.println("No hay sesión iniciada.");
                            break;
                        }
                        System.out.println("Sesión cerrada del usuario: " + usuarioLogueado.getUsername());
                        usuarioLogueado = null;
                        personajeCreado = null;
                        break;
                    }

                    case 7: {
                    	System.out.println("Usuarios: " + usuarioService.listar());
                    	break;
                    }
                    case 8: {
                        if (usuarioLogueado == null) {
                            System.out.println("Debes hacer login primero.");
                            break;
                        }

                        List<Personaje> lista = personajeService.listarPorUsuario(usuarioLogueado.getId());
                        System.out.println("Personajes del usuario " + usuarioLogueado.getUsername() + ":");
                        for (Personaje pj : lista) {
                            System.out.println(" - " + pj);
                        }
                        break;
                    }

                    case 9: {
                        if (usuarioLogueado == null) {
                            System.out.println("Debes hacer login primero.");
                            break;
                        }
                        if (personajeCreado == null || personajeCreado.getId() == null) {
                            System.out.println("Debes seleccionar/crear un personaje primero (login y elegir personaje, o crear con opción 5).");
                            break;
                        }

                        ErrorHandler.handle(() -> {
                            String tipo = Utils.pideDatoCadena(
                                "¿Qué quieres añadir? (CUERDA, PALO, PIEDRA, MOJON, HOJA, BAYA): ");
                            String t = tipo.trim().toUpperCase();

                            Equipamiento nuevo = null;

                            if ("CUERDA".equals(t)) {
                                nuevo = new Cuerda();
                            } else if ("PALO".equals(t)) {
                                nuevo = new Palo();
                            } else if ("PIEDRA".equals(t)) {
                                nuevo = new Piedra();
                            } else if ("MOJON".equals(t) || "MOJON SECO".equals(t)) {
                                nuevo = new MojonSeco();
                            } else if ("HOJA".equals(t) || "HOJA PARA LIMPIAR".equals(t)) {
                                nuevo = new HojaParaLimpiar();
                            } else if ("BAYA".equals(t)) {
                                nuevo = new Baya();
                            } else {
                                System.out.println("Tipo inválido.");
                                return;
                            }

                            EquipamientoDto añadido = equipamientoService.añadirAlInventario(personajeCreado.getId(), nuevo);
                            System.out.println("OK: añadido -> " + añadido);

                            List<EquipamientoDto> inv = equipamientoService.listarPorPersonaje(personajeCreado.getId());
                            for (EquipamientoDto ed : inv) {
                                System.out.println(" - " + ed);
                            }
                        });
                        break;
                    }

                    case 10: {
                        if (usuarioLogueado == null) {
                            System.out.println("Debes hacer login primero.");
                            break;
                        }

                        Long idPersonaje = null;

                        if (personajeCreado != null && personajeCreado.getId() != null) {
                            System.out.println("Personaje activo detectado: " + personajeCreado.getNombre()
                                    + " (id=" + personajeCreado.getId() + ")");
                            String resp = Utils.pideDatoCadena("¿Quieres recargar ese personaje? (S/N): ")
                                    .trim().toUpperCase();

                            if ("S".equals(resp)) {
                                idPersonaje = personajeCreado.getId();
                            }
                        }

                        if (idPersonaje == null) {
                            idPersonaje = Long.valueOf(Utils.pideDatoNumerico("Introduce el ID del personaje a recargar: "));
                        }

                        Personaje recargado = ErrorHandler.handleWithReturn(() ->
                            Utils.recargarPersonaje(idPersonaje)
                        );

                        if (recargado == null) break;

                        if (recargado.getUsuario() == null || recargado.getUsuario().getId() == null
                                || !recargado.getUsuario().getId().equals(usuarioLogueado.getId())) {
                            System.out.println("Ese personaje NO pertenece al usuario logueado.");
                            break;
                        }

                        personajeCreado = recargado;

                        System.out.println("\n--- PERSONAJE CARGADO ---");
                        System.out.println(personajeCreado);
                        break;
                    }
                    

                    default:
                        System.out.println("Opcion invalida");
                        break;
                }

            } catch (RuntimeException e) {
                    System.err.println("[ERRO NÃO TRATADO] " + e.getMessage());
                    e.printStackTrace();
            }
        }

        HibernateUtil.cerrarSessionFactory();
    }
    
    private static boolean esAdmin(UsuarioDto u) {
    	return u != null && "ADMINISTRADOR".equalsIgnoreCase(u.getRol());
    }
    
    private static void mostrarMenu(UsuarioDto usuarioLogueado) {
    	System.out.println("\n                     \"UNGA BUGHA\"                           ");
        System.out.println("\n--- MENU ---");

        // Siempre visibles (sin sesión)
        System.out.println("1) Registrar");
        System.out.println("2) Login");
        System.out.println("3) Salir");

        // Opciones solo si estás logueado (jugador y admin)
        if (usuarioLogueado != null) {
            System.out.println("4) Crear personaje");
            System.out.println("5) JUGAR EPISODIO ACTUAL");
            System.out.println("6) Cerrar sesión");
        }

        // Opciones extra solo ADMIN
        if (esAdmin(usuarioLogueado)) {
            System.out.println("7) Listar usuarios");
            System.out.println("8) Listar personajes por usuario");
            System.out.println("9) TEST: AÑADIR EQUIPAMIENTO POR SERVICE Y LISTAR");
            System.out.println("10) TEST: RECARGAR PERSONAJE Y MOSTRAR INVENTARIO / CRIATURAS (PERSISTENCIA)");
        }
    }
    
    
}
