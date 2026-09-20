document.addEventListener('DOMContentLoaded', function () {
  var toggle = document.querySelector('.sr-nav-toggle');
  var links = document.querySelector('.sr-nav-links');
  if (toggle && links) {
    toggle.addEventListener('click', function () {
      links.classList.toggle('open');
    });
  }

  var sidebarToggle = document.querySelector('.admin-sidebar-toggle');
  var sidebar = document.querySelector('.admin-sidebar');
  if (sidebarToggle && sidebar) {
    sidebarToggle.addEventListener('click', function () {
      sidebar.classList.toggle('open');
    });
  }

  document.querySelectorAll('[data-confirm]').forEach(function (el) {
    el.addEventListener('click', function (e) {
      if (!confirm(el.getAttribute('data-confirm'))) {
        e.preventDefault();
      }
    });
  });

  document.querySelectorAll('.js-days-calc').forEach(function (form) {
    var start = form.querySelector('[name="startDate"]');
    var end = form.querySelector('[name="endDate"]');
    if (start && end) {
      var today = new Date().toISOString().split('T')[0];
      start.setAttribute('min', today);
      start.addEventListener('change', function () { end.setAttribute('min', start.value); });
      end.setAttribute('min', today);
    }
  });
});
