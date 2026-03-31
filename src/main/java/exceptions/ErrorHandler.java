import java.util.function.Supplier;

public class ErrorHandler {

    public static void handle(Runnable action) {
        try {
            action.run();

        } catch (ReglaJuegoException e) {
            System.out.println("[REGRA DO JOGO] " + e.getMessage());

        } catch (IllegalArgumentException e) {
            System.out.println("[DADO INVÁLIDO] " + e.getMessage());

        } catch (SecurityException e) {
            System.out.println("[ACESSO NEGADO] " + e.getMessage());

        } catch (RuntimeException e) {
            System.out.println("[ERRO INESPERADO] " + e.getMessage());

        } catch (Exception e) {
            System.out.println("[ERRO CRÍTICO] Contate o administrador.");
        }
    }

    public static <T> T handleWithReturn(Supplier<T> action) {
        try {
            return action.get();

        } catch (ReglaJuegoException e) {
            System.out.println("[REGRA DO JOGO] " + e.getMessage());

        } catch (IllegalArgumentException e) {
            System.out.println("[DADO INVÁLIDO] " + e.getMessage());

        } catch (SecurityException e) {
            System.out.println("[ACESSO NEGADO] " + e.getMessage());

        } catch (RuntimeException e) {
            System.out.println("[ERRO INESPERADO] " + e.getMessage());

        } catch (Exception e) {
            System.out.println("[ERRO CRÍTICO] Contate o administrador.");
        }

        return null;
    }
}