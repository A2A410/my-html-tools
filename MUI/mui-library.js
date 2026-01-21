/* Minimal companion JS for interactive components.
   Exposes window.MUILib with small API.
*/

/*  Save/load theme */
(function(){
  const LS_KEY = 'mui_theme_v1';
  function save(pref){ try{ localStorage.setItem(LS_KEY, JSON.stringify(pref)); }catch(e){} }
  function load(){ try{ const v = localStorage.getItem(LS_KEY); return v?JSON.parse(v):{mode:'dark'} }catch(e){ return {mode:'dark'} } }

  function applyTheme(t){
    if(t.mode === 'light'){
      document.documentElement.style.setProperty('--mui-bg','#fafafa');
      document.documentElement.style.setProperty('--mui-surface','#ffffff');
      document.documentElement.style.setProperty('--mui-on-surface','#121212');
      document.documentElement.style.setProperty('--mui-muted','rgba(0,0,0,0.06)');
    } else {
      document.documentElement.style.setProperty('--mui-bg','#0f0f1a');
      document.documentElement.style.setProperty('--mui-surface','#1e1e1e');
      document.documentElement.style.setProperty('--mui-on-surface','#e6e1e5');
      document.documentElement.style.setProperty('--mui-muted','rgba(255,255,255,0.08)');
    }
  }

  /* Dialog helpers */
  function openDialog(selector){ const el = selector?document.querySelector(selector):document.querySelector('.mui-backdrop'); if(!el) return; el.classList.add('mui-open'); }
  function closeDialog(selector){ const el = selector?document.querySelector(selector):document.querySelector('.mui-backdrop'); if(!el) return; el.classList.remove('mui-open'); }

  /* Snackbar */
  function showSnackbar(msg, timeout=2800){
    let node = document.querySelector('.mui-snackbar');
    if(!node){
      node = document.createElement('div');
      node.className = 'mui-snackbar';
      document.body.appendChild(node);
    }
    node.textContent = msg;
    node.classList.remove('mui-hidden');
    setTimeout(()=>{ node.classList.add('mui-hidden') }, timeout);
  }

  /* Drawer */
  function openDrawer(selector){ const d = selector?document.querySelector(selector):document.querySelector('.mui-drawer'); if(!d) return; d.classList.add('mui-open'); }
  function closeDrawer(selector){ const d = selector?document.querySelector(selector):document.querySelector('.mui-drawer'); if(!d) return; d.classList.remove('mui-open'); }

  /* Switch toggle utility */
  function toggleSwitch(node){ if(!node) return; node.classList.toggle('mui-on'); }

  /* Init demo bindings if present */
  function initBindings(){
    document.querySelectorAll('[data-mui-toggle-theme]').forEach(btn=>{
      btn.addEventListener('click', ()=>{
        const t = load(); t.mode = t.mode === 'dark' ? 'light' : 'dark'; applyTheme(t); save(t); showSnackbar('Theme: '+t.mode);
      });
    });
    document.querySelectorAll('[data-mui-open-dialog]').forEach(btn=>{
      btn.addEventListener('click', ()=> openDialog());
    });
    document.querySelectorAll('[data-mui-close-dialog]').forEach(btn=>{
      btn.addEventListener('click', ()=> closeDialog());
    });
    document.querySelectorAll('[data-mui-open-drawer]').forEach(btn=>{
      btn.addEventListener('click', ()=> openDrawer());
    });
    document.querySelectorAll('[data-mui-close-drawer]').forEach(btn=>{
      btn.addEventListener('click', ()=> closeDrawer());
    });
    document.querySelectorAll('.mui-switch').forEach(sw=>{
      sw.addEventListener('click', ()=> toggleSwitch(sw));
    });
  }

  /* Apply saved theme on load */
  const theme = load(); applyTheme(theme);

  window.MUILib = {
    applyTheme, saveTheme: save, loadTheme: load,
    openDialog, closeDialog, showSnackbar,
    openDrawer, closeDrawer, toggleSwitch,
    initBindings
  };

  /* Auto-init on DOM ready */
  if(document.readyState === 'loading'){
    document.addEventListener('DOMContentLoaded', initBindings);
  } else initBindings();

})();