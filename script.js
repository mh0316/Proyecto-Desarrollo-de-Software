
async function downloadPDF(sectionId) {
    const { jsPDF } = window.jspdf;
    const doc = new jsPDF({ unit: 'pt', format: 'letter' });

    const element = document.getElementById(sectionId);
    let x = 40;   // margen izquierdo
    let y = 60;   // margen superior

    element.childNodes.forEach(node => {
        y = addNode(doc, node, x, y);
    });

    doc.save(sectionId + '-entrega.pdf');
}

function addNode(doc, node, x, y) {
    // Saltar nodos vacíos
    if (node.nodeType === Node.TEXT_NODE && !node.textContent.trim()) return y;

    switch (node.nodeName) {
        case 'H2':
            doc.setFont('Times','bold');
            doc.setFontSize(20);
            doc.text(node.textContent, x, y, { maxWidth: 520 });
            y += 35; // espacio extra
            break;
        case 'H3':
            doc.setFont('Times','bold');
            doc.setFontSize(16);
            doc.text(node.textContent, x, y, { maxWidth: 520 });
            y += 25;
            break;
        case 'P':
            doc.setFont('Times','normal');
            doc.setFontSize(12);
            // splitTextToSize envuelve el texto en varias líneas
            const lines = doc.splitTextToSize(node.textContent, 520);
            doc.text(lines, x, y);
            y += lines.length * 16 + 10; // alto de línea + espacio
            break;
        case 'UL':
            node.querySelectorAll('li').forEach(li => {
                doc.setFont('Times','normal');
                doc.setFontSize(12);
                const liLines = doc.splitTextToSize('• ' + li.textContent, 500);
                doc.text(liLines, x + 10, y);
                y += liLines.length * 16 + 6;
            });
            y += 10;
            break;
        default:
            // Recursión para <div> u otros contenedores
            node.childNodes.forEach(child => {
                y = addNode(doc, child, x, y);
            });
    }

    // Si te acercas al final de la página, añadir nueva página
    if (y > doc.internal.pageSize.height - 60) {
        doc.addPage();
        y = 60;
    }
    return y;
}




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