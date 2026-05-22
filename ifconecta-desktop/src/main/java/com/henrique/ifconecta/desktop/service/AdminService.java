package com.henrique.ifconecta.desktop.service;

import java.util.List;
import java.util.Map;

import com.henrique.ifconecta.desktop.core.http.ApiClient;

/** Endpoints administrativos — convites e seeder acadêmico (apenas ADMIN). */
public final class AdminService {

    private AdminService() {
    }

    /** POST /api/admin/usuarios/professores/convidar */
    public static void convidarProfessor(String nome, String emailAcad, String siape) {
        ApiClient.post("/admin/usuarios/professores/convidar",
                Map.of("nome", nome, "emailAcad", emailAcad, "siape", siape));
    }

    /** POST /api/admin/usuarios/institucionais/convidar */
    public static void convidarInstitucional(String nome, String emailAcad, String setor, String cargo) {
        ApiClient.post("/admin/usuarios/institucionais/convidar",
                Map.of("nome", nome, "emailAcad", emailAcad, "setor", setor, "cargo", cargo));
    }

    /**
     * POST /api/admin/academico/seeder — cria um curso com disciplinas.
     *
     * @param disciplinas lista de mapas {@code {nome, cargaHoraria}}
     */
    public static void criarCursoComDisciplinas(String nome, String sigla, String modalidade,
            List<Map<String, Object>> disciplinas) {
        ApiClient.post("/admin/academico/seeder", Map.of(
                "nome", nome,
                "sigla", sigla,
                "modalidade", modalidade,
                "disciplinas", disciplinas));
    }
}
