import React, { useState } from 'react';
import {
  Button, Card, Field, Input, Tabs, Textarea, useToast,
} from '../components/ui.jsx';
import { Icon } from '../components/icons.jsx';
import * as adminService from '../services/admin.js';
import { extractErrorMessage } from '../services/api.js';

function ConvidarProfessorTab() {
  const toast = useToast();
  const [form, setForm] = useState({ nome: '', emailAcad: '', siape: '' });
  const [submitting, setSubmitting] = useState(false);

  const update = (k, v) => setForm((f) => ({ ...f, [k]: v }));

  const canSubmit = form.nome.trim().length >= 3
    && /^\S+@\S+\.\S+$/.test(form.emailAcad)
    && form.siape.trim().length > 0
    && !submitting;

  const submit = async (e) => {
    e?.preventDefault();
    if (!canSubmit) return;
    setSubmitting(true);
    try {
      await adminService.convidarProfessor({
        nome: form.nome.trim(),
        emailAcad: form.emailAcad.trim(),
        siape: form.siape.trim(),
      });
      toast.success('Convite enviado!', `Email enviado para ${form.emailAcad.trim()}.`);
      setForm({ nome: '', emailAcad: '', siape: '' });
    } catch (err) {
      toast.error('Não foi possível convidar', extractErrorMessage(err));
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Card>
      <form onSubmit={submit} className="stack-md">
        <h3 style={{ margin: 0, fontSize: 15, fontWeight: 600 }}>Convidar professor</h3>
        <div style={{ fontSize: 13, color: 'var(--fg-muted)' }}>
          O professor receberá um email para definir a senha e ativar a conta.
        </div>
        <Field label="Nome completo">
          <Input icon="user" value={form.nome} onChange={(e) => update('nome', e.target.value)} placeholder="Nome completo do professor" />
        </Field>
        <Field label="Email institucional">
          <Input icon="mail" type="email" value={form.emailAcad} onChange={(e) => update('emailAcad', e.target.value)} placeholder="nome.sobrenome@ifsp.edu.br" />
        </Field>
        <Field label="SIAPE">
          <Input value={form.siape} onChange={(e) => update('siape', e.target.value)} placeholder="Matrícula SIAPE" />
        </Field>
        <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
          <Button type="submit" disabled={!canSubmit} loading={submitting} icon={<Icon name="send" size={14} />}>
            Enviar convite
          </Button>
        </div>
      </form>
    </Card>
  );
}

function ConvidarInstitucionalTab() {
  const toast = useToast();
  const [form, setForm] = useState({ nome: '', emailAcad: '', setor: '', cargo: '' });
  const [submitting, setSubmitting] = useState(false);

  const update = (k, v) => setForm((f) => ({ ...f, [k]: v }));

  const canSubmit = form.nome.trim().length >= 3
    && /^\S+@\S+\.\S+$/.test(form.emailAcad)
    && form.setor.trim().length > 0
    && form.cargo.trim().length > 0
    && !submitting;

  const submit = async (e) => {
    e?.preventDefault();
    if (!canSubmit) return;
    setSubmitting(true);
    try {
      await adminService.convidarInstitucional({
        nome: form.nome.trim(),
        emailAcad: form.emailAcad.trim(),
        setor: form.setor.trim(),
        cargo: form.cargo.trim(),
      });
      toast.success('Convite enviado!', `Email enviado para ${form.emailAcad.trim()}.`);
      setForm({ nome: '', emailAcad: '', setor: '', cargo: '' });
    } catch (err) {
      toast.error('Não foi possível convidar', extractErrorMessage(err));
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Card>
      <form onSubmit={submit} className="stack-md">
        <h3 style={{ margin: 0, fontSize: 15, fontWeight: 600 }}>Convidar servidor institucional</h3>
        <div style={{ fontSize: 13, color: 'var(--fg-muted)' }}>
          Servidores institucionais podem enviar comunicados para toda a comunidade.
        </div>
        <Field label="Nome completo">
          <Input icon="user" value={form.nome} onChange={(e) => update('nome', e.target.value)} placeholder="Nome completo do servidor" />
        </Field>
        <Field label="Email institucional">
          <Input icon="mail" type="email" value={form.emailAcad} onChange={(e) => update('emailAcad', e.target.value)} placeholder="nome.sobrenome@ifsp.edu.br" />
        </Field>
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12 }}>
          <Field label="Setor">
            <Input value={form.setor} onChange={(e) => update('setor', e.target.value)} placeholder="Ex.: CRA" />
          </Field>
          <Field label="Cargo">
            <Input value={form.cargo} onChange={(e) => update('cargo', e.target.value)} placeholder="Ex.: Coordenador" />
          </Field>
        </div>
        <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
          <Button type="submit" disabled={!canSubmit} loading={submitting} icon={<Icon name="send" size={14} />}>
            Enviar convite
          </Button>
        </div>
      </form>
    </Card>
  );
}

const SEEDER_EXEMPLO = `{
  "nome": "Análise e Desenvolvimento de Sistemas",
  "sigla": "ADS",
  "modalidade": "SUPERIOR",
  "disciplinas": [
    { "nome": "Algoritmos", "cargaHoraria": 80 },
    { "nome": "Banco de Dados", "cargaHoraria": 60 }
  ]
}`;

function SeederCursoTab() {
  const toast = useToast();
  const [json, setJson] = useState(SEEDER_EXEMPLO);
  const [submitting, setSubmitting] = useState(false);

  let parseError = null;
  let parsed = null;
  try {
    parsed = JSON.parse(json);
    if (!parsed.nome || !parsed.sigla || !parsed.modalidade) {
      parseError = 'Campos obrigatórios: nome, sigla, modalidade.';
    } else if (!Array.isArray(parsed.disciplinas)) {
      parseError = 'O campo "disciplinas" deve ser uma lista.';
    }
  } catch (e) {
    parseError = 'JSON inválido: ' + e.message;
  }

  const canSubmit = !parseError && !submitting;

  const submit = async () => {
    if (!canSubmit) return;
    setSubmitting(true);
    try {
      await adminService.seederCurso(parsed);
      toast.success('Curso criado!', `"${parsed.nome}" e ${parsed.disciplinas?.length || 0} disciplina(s) cadastrados.`);
    } catch (err) {
      toast.error('Não foi possível semear', extractErrorMessage(err));
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Card>
      <div className="stack-md">
        <h3 style={{ margin: 0, fontSize: 15, fontWeight: 600 }}>Seeder de curso</h3>
        <div style={{ fontSize: 13, color: 'var(--fg-muted)' }}>
          Cria um curso com suas disciplinas em uma única operação. Modalidades válidas: <code>SUPERIOR</code>, <code>INTEGRADO</code>, <code>SUBSEQUENTE</code>, <code>POS_GRADUACAO</code>.
        </div>
        <Field label="Payload JSON" error={parseError}>
          <Textarea
            rows={14}
            value={json}
            onChange={(e) => setJson(e.target.value)}
            style={{ fontFamily: 'var(--font-mono)', fontSize: 12.5 }}
          />
        </Field>
        <div style={{ display: 'flex', justifyContent: 'flex-end', gap: 8 }}>
          <Button variant="ghost" onClick={() => setJson(SEEDER_EXEMPLO)}>Restaurar exemplo</Button>
          <Button onClick={submit} disabled={!canSubmit} loading={submitting} icon={<Icon name="plus" size={14} />}>
            Semear
          </Button>
        </div>
      </div>
    </Card>
  );
}

export function AdminPage() {
  const [tab, setTab] = useState('professor');
  const tabs = [
    { value: 'professor', label: 'Convidar professor', icon: 'user' },
    { value: 'institucional', label: 'Convidar servidor', icon: 'shield' },
    { value: 'seeder', label: 'Seeder de cursos', icon: 'graduation' },
  ];

  return (
    <div className="app-content app-content-wide" data-screen-label="Admin">
      <div className="page-hd">
        <div className="page-hd-text">
          <h1>Admin</h1>
          <div className="page-sub">Convites e dados base do campus</div>
        </div>
      </div>
      <Tabs value={tab} onChange={setTab} items={tabs} />
      {tab === 'professor' && <ConvidarProfessorTab />}
      {tab === 'institucional' && <ConvidarInstitucionalTab />}
      {tab === 'seeder' && <SeederCursoTab />}
    </div>
  );
}
