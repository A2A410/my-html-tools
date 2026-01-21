# Material UI Library (CSS) - Ready-to-use

Files:
- material-ui-library.css  -> Core CSS tokens & components
- mui-library.js           -> Optional companion JS for interactive behavior (dialog, snackbar, drawer, theme)
- README.md                -> This file

Usage:
1. Include the CSS in your project (link tag or import).
   <link rel="stylesheet" href="material-ui-library.css">

2. Optionally include the JS to enable interactive components:
   <script src="mui-library.js"></script>

3. Use the classes (examples):
   <div class="mui-root mui-container">
     <header class="mui-appbar">
       <div class="mui-title">App</div>
       <button class="mui-btn mui-btn--outlined" data-mui-toggle-theme>Toggle</button>
     </header>
   </div>

API:
- window.MUILib.openDialog()
- window.MUILib.closeDialog()
- window.MUILib.showSnackbar(message)
- window.MUILib.openDrawer()
- window.MUILib.closeDrawer()
- window.MUILib.applyTheme({mode:'dark'|'light'})
- window.MUILib.initBindings()  // rebinds data-* hooks

License: Use as you wish.

