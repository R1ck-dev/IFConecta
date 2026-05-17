import React, { useState, useEffect, useRef, useCallback, useMemo, createContext, useContext } from 'react';
import { Icon } from './icons.jsx';

export function Button({
  variant = 'primary',
  size = 'md',
  icon, iconRight,
  block,
  loading,
  disabled,
  className = '',
  children,
  ...props
}) {
  const cls = [
    'btn',
    `btn-${variant}`,
    size !== 'md' && `btn-${size}`,
    block && 'btn-block',
    !children && (icon || iconRight) && 'btn-icon',
    className,
  ].filter(Boolean).join(' ');
  return (
    <button className={cls} disabled={disabled || loading} {...props}>
      {loading ? <span className="spinner" style={{ borderTopColor: 'currentColor' }} /> : icon}
      {children}
      {!loading && iconRight}
    </button>
  );
}

export function Card({ className = '', hoverable, flat, tight, children, ...props }) {
  return (
    <div
      className={[
        'card',
        tight && 'card-tight',
        hoverable && 'card-hoverable',
        flat && 'card-flat',
        className,
      ].filter(Boolean).join(' ')}
      {...props}
    >
      {children}
    </div>
  );
}

const AVATAR_COLORS = [
  ['#3b82f6', '#dbeafe'],
  ['#10b981', '#d1fae5'],
  ['#f59e0b', '#fef3c7'],
  ['#ef4444', '#fee2e2'],
  ['#8b5cf6', '#ede9fe'],
  ['#ec4899', '#fce7f3'],
  ['#06b6d4', '#cffafe'],
  ['#f97316', '#ffedd5'],
];

function hashStr(s) {
  let h = 0;
  for (let i = 0; i < s.length; i++) h = ((h << 5) - h) + s.charCodeAt(i);
  return Math.abs(h);
}

export function Avatar({ name = '?', size = 'md', anonymous, className = '', style }) {
  if (anonymous) {
    return (
      <div className={`avatar avatar-${size} avatar-anon ${className}`} style={style} aria-label="Anônimo">
        <Icon name="user" size={size === 'xl' ? 36 : size === 'lg' ? 20 : 16} />
      </div>
    );
  }
  const initials = name.split(' ').filter(Boolean).slice(0, 2).map(w => w[0]).join('').toUpperCase() || '?';
  const [fg, bg] = AVATAR_COLORS[hashStr(name) % AVATAR_COLORS.length];
  return (
    <div
      className={`avatar avatar-${size} ${className}`}
      style={{ background: bg, color: fg, ...style }}
      aria-label={name}
    >
      {initials}
    </div>
  );
}

export function Badge({ variant = 'default', dot, children, className = '', ...props }) {
  const cls = [
    'badge',
    variant !== 'default' && `badge-${variant}`,
    dot && 'badge-dot',
    className,
  ].filter(Boolean).join(' ');
  return <span className={cls} {...props}>{children}</span>;
}

export function Input({ icon, error, className = '', ...props }) {
  const input = (
    <input
      className={`input ${error ? 'has-error' : ''} ${className}`}
      {...props}
    />
  );
  if (icon) {
    return (
      <div className="input-prefix-wrap">
        <span className="prefix"><Icon name={icon} size={16} /></span>
        {input}
      </div>
    );
  }
  return input;
}

export const Textarea = React.forwardRef(function Textarea({ error, className = '', ...props }, ref) {
  return <textarea ref={ref} className={`textarea ${error ? 'has-error' : ''} ${className}`} {...props} />;
});

export function Select({ error, className = '', children, ...props }) {
  return (
    <select className={`input select ${error ? 'has-error' : ''} ${className}`} {...props}>
      {children}
    </select>
  );
}

export function Field({ label, hint, error, success, children, htmlFor, rightLabel }) {
  return (
    <div className="field">
      {label && (
        <label className="field-label" htmlFor={htmlFor}>
          <span>{label}</span>
          {rightLabel && <span style={{ fontSize: 12, fontWeight: 400, color: 'var(--fg-subtle)' }}>{rightLabel}</span>}
        </label>
      )}
      {children}
      {error && <div className="field-error"><Icon name="alertCircle" size={13} /> {error}</div>}
      {success && !error && <div className="field-success"><Icon name="check" size={13} /> {success}</div>}
      {hint && !error && !success && <div className="field-hint">{hint}</div>}
    </div>
  );
}

export function Checkbox({ checked, onChange, label, ...props }) {
  return (
    <label className="checkbox">
      <input type="checkbox" checked={!!checked} onChange={(e) => onChange?.(e.target.checked)} {...props} />
      {label && <span>{label}</span>}
    </label>
  );
}

export function RadioCard({ checked, onClick, title, desc, icon }) {
  return (
    <div className={`radio ${checked ? 'checked' : ''}`} onClick={onClick}>
      <input type="radio" checked={checked} readOnly />
      <div style={{ flex: 1 }}>
        <div className="radio-label" style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
          {icon && <Icon name={icon} size={15} />}
          {title}
        </div>
        {desc && <div className="radio-desc">{desc}</div>}
      </div>
    </div>
  );
}

export function Tabs({ value, onChange, items }) {
  return (
    <div className="tabs" role="tablist">
      {items.map((it) => (
        <button
          key={it.value}
          role="tab"
          aria-selected={value === it.value}
          className={`tab ${value === it.value ? 'active' : ''}`}
          onClick={() => onChange(it.value)}
        >
          {it.icon && <Icon name={it.icon} size={15} />}
          {it.label}
          {it.count != null && <span className="tab-count">{it.count}</span>}
        </button>
      ))}
    </div>
  );
}

export function Dialog({ open, onClose, title, subtitle, children, footer, width }) {
  useEffect(() => {
    if (!open) return;
    const onKey = (e) => { if (e.key === 'Escape') onClose?.(); };
    document.addEventListener('keydown', onKey);
    document.body.style.overflow = 'hidden';
    return () => {
      document.removeEventListener('keydown', onKey);
      document.body.style.overflow = '';
    };
  }, [open, onClose]);

  if (!open) return null;
  return (
    <div className="dlg-backdrop" onMouseDown={(e) => { if (e.target === e.currentTarget) onClose?.(); }}>
      <div className="dlg" style={width ? { maxWidth: width } : undefined} role="dialog" aria-modal="true">
        {(title || subtitle) && (
          <div className="dlg-hd">
            <div>
              <div className="dlg-title">{title}</div>
              {subtitle && <div className="dlg-sub">{subtitle}</div>}
            </div>
            <button className="hdr-icon-btn" onClick={onClose} aria-label="Fechar" style={{ width: 32, height: 32 }}>
              <Icon name="x" size={18} />
            </button>
          </div>
        )}
        <div className="dlg-body">{children}</div>
        {footer && <div className="dlg-ft">{footer}</div>}
      </div>
    </div>
  );
}

export function Dropdown({ trigger, children, align = 'right' }) {
  const [open, setOpen] = useState(false);
  const ref = useRef(null);
  useEffect(() => {
    if (!open) return;
    const onDoc = (e) => {
      if (ref.current && !ref.current.contains(e.target)) setOpen(false);
    };
    const onKey = (e) => { if (e.key === 'Escape') setOpen(false); };
    document.addEventListener('mousedown', onDoc);
    document.addEventListener('keydown', onKey);
    return () => {
      document.removeEventListener('mousedown', onDoc);
      document.removeEventListener('keydown', onKey);
    };
  }, [open]);

  return (
    <div ref={ref} style={{ position: 'relative', display: 'inline-block' }}>
      {React.cloneElement(trigger, { onClick: (e) => { e.stopPropagation(); setOpen(!open); }, 'aria-expanded': open })}
      {open && (
        <div className="menu" style={{ top: 'calc(100% + 6px)', [align]: 0 }}>
          {typeof children === 'function' ? children({ close: () => setOpen(false) }) : children}
        </div>
      )}
    </div>
  );
}

export function MenuItem({ icon, danger, onClick, children }) {
  return (
    <button className={`menu-item ${danger ? 'danger' : ''}`} onClick={onClick}>
      {icon && <Icon name={icon} size={15} />}
      {children}
    </button>
  );
}

const ToastCtx = createContext(null);

export function ToastProvider({ children }) {
  const [toasts, setToasts] = useState([]);
  const idRef = useRef(0);
  const push = useCallback((t) => {
    const id = ++idRef.current;
    setToasts(prev => [...prev, { id, ...t }]);
    setTimeout(() => setToasts(prev => prev.filter(x => x.id !== id)), t.duration || 3500);
  }, []);
  const api = useMemo(() => ({
    toast: push,
    success: (title, msg) => push({ title, msg, variant: 'success' }),
    error: (title, msg) => push({ title, msg, variant: 'error' }),
    info: (title, msg) => push({ title, msg, variant: 'default' }),
  }), [push]);
  return (
    <ToastCtx.Provider value={api}>
      {children}
      <div className="toast-wrap">
        {toasts.map(t => (
          <div key={t.id} className={`toast ${t.variant === 'success' ? 'success' : t.variant === 'error' ? 'error' : ''}`}>
            <div style={{ flex: 1 }}>
              <div className="toast-title">{t.title}</div>
              {t.msg && <div className="toast-msg">{t.msg}</div>}
            </div>
          </div>
        ))}
      </div>
    </ToastCtx.Provider>
  );
}

export function useToast() {
  const ctx = useContext(ToastCtx);
  if (!ctx) throw new Error('useToast must be used within ToastProvider');
  return ctx;
}

export function Skel({ w = '100%', h = 14, r = 6, style }) {
  return <div className="skel" style={{ width: w, height: h, borderRadius: r, ...style }} />;
}

export function Empty({ icon = 'inbox', title, message, action }) {
  return (
    <div className="empty">
      <div className="empty-icon"><Icon name={icon} size={24} /></div>
      <h3>{title}</h3>
      {message && <p>{message}</p>}
      {action}
    </div>
  );
}

export function timeAgo(date) {
  const now = new Date();
  const d = date instanceof Date ? date : new Date(date);
  const diff = (now - d) / 1000;
  if (diff < 60) return 'agora';
  if (diff < 3600) return `há ${Math.floor(diff / 60)}m`;
  if (diff < 86400) return `há ${Math.floor(diff / 3600)}h`;
  if (diff < 172800) return 'ontem';
  if (diff < 604800) return `há ${Math.floor(diff / 86400)} dias`;
  if (diff < 2592000) return `há ${Math.floor(diff / 604800)} sem`;
  const months = Math.floor(diff / 2592000);
  if (months < 12) return `há ${months} mês${months > 1 ? 'es' : ''}`;
  return d.toLocaleDateString('pt-BR');
}

export function pwScore(pw) {
  if (!pw) return 0;
  let s = 0;
  if (pw.length >= 6) s++;
  if (pw.length >= 10) s++;
  if (/[A-Z]/.test(pw) && /[a-z]/.test(pw)) s++;
  if (/\d/.test(pw)) s++;
  if (/[^A-Za-z0-9]/.test(pw)) s++;
  return Math.min(s, 4);
}

export function PwStrength({ pw }) {
  const score = pwScore(pw);
  const labels = ['', 'Fraca', 'Razoável', 'Boa', 'Forte'];
  const klass = score <= 1 ? 'on-weak' : score <= 2 ? 'on-med' : 'on-strong';
  return (
    <div>
      <div className="pw-strength">
        {[0, 1, 2, 3].map(i => (
          <div key={i} className={`pw-strength-bar ${i < score ? klass : ''}`} />
        ))}
      </div>
      {pw && <div className="field-hint" style={{ marginTop: 6 }}>Senha: <b style={{ color: 'var(--fg)' }}>{labels[score]}</b></div>}
    </div>
  );
}
