/* Main JavaScript - Pure JavaScript (No frameworks) */

document.addEventListener('DOMContentLoaded', function() {
    console.log('Digital Marketplace loaded');
    
    // Smooth scroll for navigation links
    const navLinks = document.querySelectorAll('.nav-links a');
    navLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            // Allow default navigation for different pages
            if (this.getAttribute('href').startsWith('#')) {
                e.preventDefault();
                const targetId = this.getAttribute('href');
                const targetSection = document.querySelector(targetId);
                if (targetSection) {
                    targetSection.scrollIntoView({ behavior: 'smooth' });
                }
            }
        });
    });

    // Form validation for contact form
    const contactForm = document.querySelector('.contact-form form');
    if (contactForm) {
        contactForm.addEventListener('submit', function(e) {
            const name = document.getElementById('name').value.trim();
            const email = document.getElementById('email').value.trim();
            const subject = document.getElementById('subject').value.trim();
            const message = document.getElementById('message').value.trim();

            if (!name || !email || !subject || !message) {
                e.preventDefault();
                alert('Please fill in all fields');
                return;
            }

            if (!isValidEmail(email)) {
                e.preventDefault();
                alert('Please enter a valid email address');
                return;
            }
        });
    }

    // Email validation helper
    function isValidEmail(email) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    }

    // Active navigation link highlighting
    const currentLocation = location.pathname;
    const menuItems = document.querySelectorAll('.nav-links a');
    menuItems.forEach(item => {
        const href = item.getAttribute('href');
        if (href === currentLocation) {
            item.style.color = '#007bff';
            item.style.fontWeight = 'bold';
        }
    });

    // Mobile menu toggle (if needed)
    const mobileMenuBtn = document.querySelector('.mobile-menu-btn');
    const navMenu = document.querySelector('.nav-links');
    if (mobileMenuBtn && navMenu) {
        mobileMenuBtn.addEventListener('click', function() {
            navMenu.classList.toggle('active');
        });
    }

    // Add hover effect to product cards
    const productCards = document.querySelectorAll('.product-card');
    productCards.forEach(card => {
        card.addEventListener('mouseenter', function() {
            this.style.transition = 'all 0.3s ease';
        });
    });

    // Button click handlers
    const buttons = document.querySelectorAll('.btn');
    buttons.forEach(button => {
        button.addEventListener('click', function(e) {
            // Ripple effect
            const rect = this.getBoundingClientRect();
            const x = e.clientX - rect.left;
            const y = e.clientY - rect.top;
            
            const ripple = document.createElement('span');
            ripple.style.position = 'absolute';
            ripple.style.width = '20px';
            ripple.style.height = '20px';
            ripple.style.background = 'rgba(255, 255, 255, 0.5)';
            ripple.style.borderRadius = '50%';
            ripple.style.left = x + 'px';
            ripple.style.top = y + 'px';
            ripple.style.pointerEvents = 'none';
            ripple.style.animation = 'ripple 0.6s ease-out';
            
            this.style.position = 'relative';
            this.style.overflow = 'hidden';
            this.appendChild(ripple);
            
            setTimeout(() => ripple.remove(), 600);
        });
    });
});

// Animation keyframe injection
const style = document.createElement('style');
style.textContent = `
    @keyframes ripple {
        to {
            transform: scale(4);
            opacity: 0;
        }
    }
`;
document.head.appendChild(style);
