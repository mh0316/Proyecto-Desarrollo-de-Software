// Smooth scrolling para enlaces del navbar
document.addEventListener('DOMContentLoaded', function() {
    // Smooth scroll para enlaces de navegación
    const navLinks = document.querySelectorAll('.navbar-nav .nav-link[href^="#"]');

    navLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();

            const targetId = this.getAttribute('href');
            const targetElement = document.querySelector(targetId);

            if (targetElement) {
                // Cerrar el navbar collapse en móviles
                const navbarCollapse = document.querySelector('.navbar-collapse');
                if (navbarCollapse.classList.contains('show')) {
                    const bsCollapse = new bootstrap.Collapse(navbarCollapse);
                    bsCollapse.hide();
                }

                // Scroll suave al elemento
                const offsetTop = targetElement.offsetTop - 80; // 80px para compensar la navbar fija

                window.scrollTo({
                    top: offsetTop,
                    behavior: 'smooth'
                });

                // Abrir el collapse automáticamente
                setTimeout(() => {
                    const collapseTarget = targetElement.querySelector('.collapse');
                    if (collapseTarget && !collapseTarget.classList.contains('show')) {
                        const bsCollapse = new bootstrap.Collapse(collapseTarget);
                        bsCollapse.show();
                    }
                }, 500);
            }
        });
    });

    // Highlight active section in navbar
    function updateActiveNav() {
        const sections = document.querySelectorAll('.portfolio-section');
        const navLinks = document.querySelectorAll('.navbar-nav .nav-link[href^="#"]');

        let currentSection = '';

        sections.forEach(section => {
            const sectionTop = section.getBoundingClientRect().top;
            const sectionHeight = section.offsetHeight;

            if (sectionTop <= 100 && sectionTop + sectionHeight > 100) {
                currentSection = section.getAttribute('id');
            }
        });

        navLinks.forEach(link => {
            link.classList.remove('active');
            if (link.getAttribute('href') === `#${currentSection}`) {
                link.classList.add('active');
            }
        });
    }

    // Actualizar navegación activa al hacer scroll
    window.addEventListener('scroll', updateActiveNav);

    // Animación de entrada para las cards
    const observerOptions = {
        threshold: 0.1,
        rootMargin: '0px 0px -50px 0px'
    };

    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.style.opacity = '1';
                entry.target.style.transform = 'translateY(0)';
            }
        });
    }, observerOptions);

    // Aplicar animación a las secciones del portafolio
    const portfolioSections = document.querySelectorAll('.portfolio-section');
    portfolioSections.forEach(section => {
        section.style.opacity = '0';
        section.style.transform = 'translateY(20px)';
        section.style.transition = 'opacity 0.6s ease, transform 0.6s ease';
        observer.observe(section);
    });

    // Mejorar las animaciones de colapso
    const collapseElements = document.querySelectorAll('.collapse');
    collapseElements.forEach(collapse => {
        collapse.addEventListener('show.bs.collapse', function() {
            const header = this.previousElementSibling;
            header.setAttribute('aria-expanded', 'true');
        });

        collapse.addEventListener('hide.bs.collapse', function() {
            const header = this.previousElementSibling;
            header.setAttribute('aria-expanded', 'false');
        });
    });

    // Tooltips para badges (opcional)
    const badges = document.querySelectorAll('.badge');
    badges.forEach(badge => {
        if (badge.textContent.match(/^[A-Z]+\d+$/)) {
            badge.setAttribute('title', `Requerimiento ${badge.textContent}`);
        }
    });

    // Initialize Bootstrap tooltips
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
});

// Función para copiar enlace de sección
function copyLink(sectionId) {
    const url = `${window.location.origin}${window.location.pathname}#${sectionId}`;
    navigator.clipboard.writeText(url).then(() => {
        // Mostrar feedback visual
        const toast = document.createElement('div');
        toast.className = 'toast-notification';
        toast.textContent = 'Enlace copiado al portapapeles';
        toast.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            background: #28a745;
            color: white;
            padding: 10px 20px;
            border-radius: 5px;
            z-index: 9999;
            opacity: 0;
            transition: opacity 0.3s ease;
        `;

        document.body.appendChild(toast);

        setTimeout(() => {
            toast.style.opacity = '1';
        }, 100);

        setTimeout(() => {
            toast.style.opacity = '0';
            setTimeout(() => {
                document.body.removeChild(toast);
            }, 300);
        }, 2000);
    });
}