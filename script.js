function toggleSidebar() {
    const sidebar = document.getElementById('sidebar');
    const overlay = document.querySelector('.sidebar-overlay');
    
    sidebar.classList.toggle('show');
    overlay.classList.toggle('show');
}

// Smooth scrolling para los enlaces del sidebar
document.querySelectorAll('.sidebar .nav-link').forEach(link => {
    link.addEventListener('click', function(e) {
        e.preventDefault();
        
        // Remover clase active de todos los enlaces
        document.querySelectorAll('.sidebar .nav-link').forEach(l => l.classList.remove('active'));
        
        // Agregar clase active al enlace clickeado
        this.classList.add('active');
        
        // Scroll suave a la sección
        const targetId = this.getAttribute('href');
        const targetSection = document.querySelector(targetId);
        
        if (targetSection) {
            targetSection.scrollIntoView({
                behavior: 'smooth',
                block: 'start'
            });
        }
        
        // Cerrar sidebar en móvil después del click
        if (window.innerWidth <= 991) {
            toggleSidebar();
        }
    });
});

// Activar enlace según la sección visible
window.addEventListener('scroll', function() {
    const sections = document.querySelectorAll('section');
    const navLinks = document.querySelectorAll('.sidebar .nav-link');
    
    let current = '';
    
    sections.forEach(section => {
        const sectionTop = section.offsetTop - 100; // Offset para el header fijo
        const sectionHeight = section.offsetHeight;
        
        if (window.scrollY >= sectionTop && window.scrollY < sectionTop + sectionHeight) {
            current = section.getAttribute('id');
        }
    });
    
    navLinks.forEach(link => {
        link.classList.remove('active');
        if (link.getAttribute('href') === '#' + current) {
            link.classList.add('active');
        }
    });
});