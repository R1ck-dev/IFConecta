import React, { useCallback, useEffect, useMemo, useState } from 'react';
import {
  Badge, Button, Card, Empty, Input, Select, Skel, Tabs, useToast,
} from '../components/ui.jsx';
import { Icon } from '../components/icons.jsx';
import { CreateTurmaDialog } from '../components/modals.jsx';
import { useAuth } from '../store/AuthContext.jsx';
import * as academicoService from '../services/academico.js';
import * as cursosService from '../services/cursos.js';
import { extractErrorMessage } from '../services/api.js';

function TurmaCard({ turma, action, footerExtra }) {
  return (
    <Card>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', gap: 12 }}>
        <div style={{ minWidth: 0, flex: 1 }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 8, flexWrap: 'wrap' }}>
            <Badge variant="primary">{turma.cursoSigla || 'CURSO'}</Badge>
            <span className="text-xs text-subtle">{turma.codigoTurma}</span>
            <span className="text-xs text-subtle">· {turma.semestre}</span>
          </div>
          <h3 style={{ margin: '6px 0 4px', fontSize: 16, fontWeight: 700, letterSpacing: '-0.01em' }}>
            {turma.disciplinaNome}
          </h3>
          <div style={{ fontSize: 13, color: 'var(--fg-muted)' }}>{turma.professorNome}</div>
        </div>
        {action}
      </div>
      <div className="divider" />
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: 12, fontSize: 12 }}>
        <div>
          <div className="text-subtle text-xs">Carga horária</div>
          <div className="fw-600" style={{ marginTop: 2 }}>{turma.cargaHoraria || '—'} h</div>
        </div>
        <div>
          <div className="text-subtle text-xs">Matriculados</div>
          <div className="fw-600" style={{ marginTop: 2 }}>{turma.qtdMatriculados}</div>
        </div>
        <div>
          <div className="text-subtle text-xs">Curso</div>
          <div className="fw-600" style={{ marginTop: 2, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }} title={turma.cursoNome}>
            {turma.cursoNome || '—'}
          </div>
        </div>
      </div>
      {footerExtra}
    </Card>
  );
}

function TurmaSkeleton() {
  return (
    <Card>
      <Skel w="40%" h={14} />
      <Skel w="80%" h={16} style={{ marginTop: 8 }} />
      <Skel w="50%" h={12} style={{ marginTop: 6 }} />
    </Card>
  );
}

function MinhasTurmasTab({ onChange }) {
  const toast = useToast();
  const [turmas, setTurmas] = useState([]);
  const [loading, setLoading] = useState(true);
  const [cancelandoId, setCancelandoId] = useState(null);

  const carregar = useCallback(async () => {
    setLoading(true);
    try {
      const lista = await academicoService.listarMinhasTurmas();
      setTurmas(lista);
    } catch (err) {
      toast.error('Não foi possível carregar suas turmas', extractErrorMessage(err));
    } finally {
      setLoading(false);
    }
  }, [toast]);

  useEffect(() => { carregar(); }, [carregar]);

  const sair = async (turmaId) => {
    if (!window.confirm('Deseja realmente sair desta turma?')) return;
    setCancelandoId(turmaId);
    try {
      await academicoService.cancelarMatricula(turmaId);
      toast.success('Você saiu da turma.');
      await carregar();
      onChange?.();
    } catch (err) {
      toast.error('Não foi possível sair', extractErrorMessage(err));
    } finally {
      setCancelandoId(null);
    }
  };

  if (loading) {
    return <div className="grid-2"><TurmaSkeleton /><TurmaSkeleton /></div>;
  }
  if (turmas.length === 0) {
    return <Empty icon="bookOpen" title="Sem turmas ainda" message="Procure uma turma na aba 'Buscar turmas' para se matricular." />;
  }
  return (
    <div className="grid-2">
      {turmas.map((t) => (
        <TurmaCard
          key={t.id}
          turma={t}
          action={
            <Button size="sm" variant="outline" loading={cancelandoId === t.id} onClick={() => sair(t.id)}>
              Sair
            </Button>
          }
        />
      ))}
    </div>
  );
}

function LecionadasTab({ refreshSignal }) {
  const toast = useToast();
  const [turmas, setTurmas] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let cancelled = false;
    setLoading(true);
    academicoService.listarTurmasLecionadas()
      .then((lista) => { if (!cancelled) setTurmas(lista); })
      .catch((err) => { if (!cancelled) toast.error('Não foi possível carregar', extractErrorMessage(err)); })
      .finally(() => { if (!cancelled) setLoading(false); });
    return () => { cancelled = true; };
  }, [refreshSignal]);

  if (loading) {
    return <div className="grid-2"><TurmaSkeleton /><TurmaSkeleton /></div>;
  }
  if (turmas.length === 0) {
    return <Empty icon="briefcase" title="Nenhuma turma" message="Você ainda não tem turmas atribuídas." />;
  }
  return (
    <div className="grid-2">
      {turmas.map((t) => <TurmaCard key={t.id} turma={t} />)}
    </div>
  );
}

function BuscarTurmasTab({ minhasIds, onMatriculado }) {
  const toast = useToast();
  const [disciplinas, setDisciplinas] = useState([]);
  const [filtroDisciplina, setFiltroDisciplina] = useState('');
  const [filtroSemestre, setFiltroSemestre] = useState('');
  const [itens, setItens] = useState([]);
  const [pagina, setPagina] = useState(0);
  const [totalPaginas, setTotalPaginas] = useState(0);
  const [loading, setLoading] = useState(true);
  const [loadingMais, setLoadingMais] = useState(false);
  const [matriculandoId, setMatriculandoId] = useState(null);

  useEffect(() => {
    academicoService.listarDisciplinas()
      .then(setDisciplinas)
      .catch(() => setDisciplinas([]));
  }, []);

  const carregar = useCallback(async (pg = 0) => {
    const isFirst = pg === 0;
    if (isFirst) setLoading(true); else setLoadingMais(true);
    try {
      const data = await academicoService.listarTurmas({
        pagina: pg,
        tamanho: 12,
        disciplinaId: filtroDisciplina || undefined,
        semestre: filtroSemestre || undefined,
      });
      setItens((prev) => (isFirst ? data.itens : [...prev, ...data.itens]));
      setPagina(data.paginaAtual);
      setTotalPaginas(data.totalPaginas);
    } catch (err) {
      toast.error('Não foi possível carregar turmas', extractErrorMessage(err));
    } finally {
      setLoading(false);
      setLoadingMais(false);
    }
  }, [filtroDisciplina, filtroSemestre, toast]);

  useEffect(() => { carregar(0); }, [carregar]);

  const matricular = async (turmaId) => {
    setMatriculandoId(turmaId);
    try {
      await academicoService.matricularTurma(turmaId);
      toast.success('Matrícula realizada!', 'Você foi adicionado(a) à turma.');
      onMatriculado?.();
    } catch (err) {
      toast.error('Não foi possível matricular', extractErrorMessage(err));
    } finally {
      setMatriculandoId(null);
    }
  };

  return (
    <>
      <div className="filter-bar">
        <div style={{ minWidth: 220 }}>
          <Select value={filtroDisciplina} onChange={(e) => setFiltroDisciplina(e.target.value)}>
            <option value="">Todas as disciplinas</option>
            {disciplinas.map((d) => (
              <option key={d.id} value={d.id}>
                {d.cursoSigla ? `${d.cursoSigla} — ${d.nome}` : d.nome}
              </option>
            ))}
          </Select>
        </div>
        <div style={{ minWidth: 140 }}>
          <Input
            placeholder="Semestre (ex.: 2026.1)"
            value={filtroSemestre}
            onChange={(e) => setFiltroSemestre(e.target.value)}
          />
        </div>
      </div>

      {loading ? (
        <div className="grid-2"><TurmaSkeleton /><TurmaSkeleton /></div>
      ) : itens.length === 0 ? (
        <Empty icon="search" title="Nenhuma turma encontrada" message="Tente outro filtro ou volte mais tarde." />
      ) : (
        <>
          <div className="grid-2">
            {itens.map((t) => {
              const jaMatriculado = minhasIds.has(t.id);
              return (
                <TurmaCard
                  key={t.id}
                  turma={t}
                  action={
                    jaMatriculado ? (
                      <Badge variant="success" dot>Matriculado</Badge>
                    ) : (
                      <Button
                        size="sm"
                        icon={<Icon name="plus" size={13} />}
                        loading={matriculandoId === t.id}
                        onClick={() => matricular(t.id)}
                      >
                        Matricular
                      </Button>
                    )
                  }
                />
              );
            })}
          </div>
          {pagina + 1 < totalPaginas && (
            <div style={{ textAlign: 'center', padding: 12 }}>
              <Button variant="outline" loading={loadingMais} onClick={() => carregar(pagina + 1)}>
                Carregar mais
              </Button>
            </div>
          )}
        </>
      )}
    </>
  );
}

function CursosTab() {
  const [cursos, setCursos] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    cursosService.listar()
      .then(setCursos)
      .catch(() => setCursos([]))
      .finally(() => setLoading(false));
  }, []);

  if (loading) {
    return <div className="grid-clubes"><Card><Skel w="40%" h={14} /></Card><Card><Skel w="40%" h={14} /></Card></div>;
  }
  if (cursos.length === 0) {
    return <Empty icon="graduation" title="Sem cursos cadastrados" message="Cursos aparecem aqui após serem semeados pelo admin." />;
  }
  return (
    <div className="grid-clubes">
      {cursos.map((c) => (
        <Card key={c.id} hoverable>
          <div style={{ display: 'flex', alignItems: 'center', gap: 12, marginBottom: 8 }}>
            <div style={{
              width: 38, height: 38, borderRadius: 10,
              background: 'var(--primary-soft)', color: 'var(--primary)',
              display: 'grid', placeItems: 'center', fontWeight: 700, fontSize: 13.5,
              letterSpacing: '-0.02em',
            }}>{c.sigla}</div>
            <Badge variant={c.modalidade === 'SUPERIOR' ? 'primary' : 'default'}>
              {c.modalidade?.replace('_', ' ') || '—'}
            </Badge>
          </div>
          <h3 style={{ margin: '4px 0 4px', fontSize: 15, fontWeight: 700, lineHeight: 1.25, letterSpacing: '-0.01em' }}>
            {c.nome}
          </h3>
        </Card>
      ))}
    </div>
  );
}

export function AcademicoPage() {
  const { me } = useAuth();
  const isProf = me?.tipo === 'PROFESSOR';
  const isAluno = me?.tipo === 'ALUNO';

  const defaultTab = isProf ? 'leciono' : isAluno ? 'matriculadas' : 'cursos';
  const [tab, setTab] = useState(defaultTab);
  const [minhasIds, setMinhasIds] = useState(new Set());
  const [refreshMinhas, setRefreshMinhas] = useState(0);
  const [refreshLeciono, setRefreshLeciono] = useState(0);
  const [createTurmaOpen, setCreateTurmaOpen] = useState(false);

  useEffect(() => {
    if (!isAluno) return;
    let cancelled = false;
    academicoService.listarMinhasTurmas()
      .then((lista) => { if (!cancelled) setMinhasIds(new Set(lista.map((t) => t.id))); })
      .catch(() => {});
    return () => { cancelled = true; };
  }, [isAluno, refreshMinhas]);

  const bumpRefresh = () => setRefreshMinhas((s) => s + 1);

  const tabs = useMemo(() => {
    if (isProf) {
      return [
        { value: 'leciono', label: 'Turmas que leciono', icon: 'briefcase' },
        { value: 'cursos', label: 'Cursos', icon: 'graduation' },
      ];
    }
    if (isAluno) {
      return [
        { value: 'matriculadas', label: 'Minhas turmas', icon: 'bookOpen' },
        { value: 'buscar', label: 'Buscar turmas', icon: 'search' },
        { value: 'cursos', label: 'Cursos', icon: 'graduation' },
      ];
    }
    return [{ value: 'cursos', label: 'Cursos', icon: 'graduation' }];
  }, [isProf, isAluno]);

  return (
    <div className="app-content app-content-wide" data-screen-label="Academico">
      <div className="page-hd">
        <div className="page-hd-text">
          <h1>Acadêmico</h1>
          <div className="page-sub">Suas turmas, disciplinas e cursos do campus</div>
        </div>
        {isProf && tab === 'leciono' && (
          <Button icon={<Icon name="plus" size={14} />} onClick={() => setCreateTurmaOpen(true)}>
            Nova turma
          </Button>
        )}
      </div>

      <Tabs value={tab} onChange={setTab} items={tabs} />

      {tab === 'matriculadas' && <MinhasTurmasTab onChange={bumpRefresh} />}
      {tab === 'buscar' && <BuscarTurmasTab minhasIds={minhasIds} onMatriculado={bumpRefresh} />}
      {tab === 'leciono' && <LecionadasTab refreshSignal={refreshLeciono} />}
      {tab === 'cursos' && <CursosTab />}

      <CreateTurmaDialog
        open={createTurmaOpen}
        onClose={() => setCreateTurmaOpen(false)}
        onCreated={() => setRefreshLeciono((s) => s + 1)}
      />
    </div>
  );
}
