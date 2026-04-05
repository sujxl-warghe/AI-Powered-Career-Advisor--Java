// ── Tab Switching ─────────────────────────────────────────────────
function showTab(tabId) {
    // Hide all tab contents
    document.querySelectorAll('.tab-content').forEach(function (el) {
        el.classList.remove('active');
    });

    // Deactivate all tab buttons
    document.querySelectorAll('.tab-btn').forEach(function (btn) {
        btn.classList.remove('active');
    });

    // Show selected tab
    const target = document.getElementById('tab-' + tabId);
    if (target) target.classList.add('active');

    // Find and activate the clicked button
    document.querySelectorAll('.tab-btn').forEach(function (btn) {
        if (btn.getAttribute('onclick') === "showTab('" + tabId + "')") {
            btn.classList.add('active');
        }
    });
}

// ── Copy AI Content ───────────────────────────────────────────────
function copyText(elementId) {
    const el = document.getElementById(elementId);
    if (!el) return;

    const text = el.innerText || el.textContent;

    if (navigator.clipboard && window.isSecureContext) {
        navigator.clipboard.writeText(text).then(function () {
            showCopyFeedback();
        }).catch(function () {
            fallbackCopy(text);
        });
    } else {
        fallbackCopy(text);
    }
}

function fallbackCopy(text) {
    const textarea = document.createElement('textarea');
    textarea.value = text;
    textarea.style.position = 'fixed';
    textarea.style.opacity = '0';
    document.body.appendChild(textarea);
    textarea.select();
    try {
        document.execCommand('copy');
        showCopyFeedback();
    } catch (e) {
        alert('Copy failed. Please select the text manually.');
    }
    document.body.removeChild(textarea);
}

function showCopyFeedback() {
    const existing = document.getElementById('copyToast');
    if (existing) existing.remove();

    const toast = document.createElement('div');
    toast.id = 'copyToast';
    toast.textContent = '✅ Copied to clipboard!';
    Object.assign(toast.style, {
        position: 'fixed',
        bottom: '1.5rem',
        right: '1.5rem',
        background: '#10b981',
        color: 'white',
        padding: '.625rem 1.25rem',
        borderRadius: '8px',
        fontWeight: '600',
        fontSize: '.875rem',
        boxShadow: '0 4px 6px rgba(0,0,0,.1)',
        zIndex: '9999',
        transition: 'opacity .3s'
    });
    document.body.appendChild(toast);

    setTimeout(function () {
        toast.style.opacity = '0';
        setTimeout(function () { toast.remove(); }, 300);
    }, 2500);
}

// ── Score Circle Progress ─────────────────────────────────────────
function animateScoreCircle() {
    const circle = document.querySelector('.match-score-circle');
    if (!circle) return;
    const score = parseFloat(circle.dataset.score) || 0;

    let pct = 0;
    const target = score;
    const duration = 1000;
    const start = performance.now();

    function step(now) {
        const elapsed = now - start;
        pct = Math.min(target, (elapsed / duration) * target);
        circle.style.background = 'conic-gradient(var(--primary) ' + pct + '%, var(--border) 0%)';

        if (score >= 80) circle.style.background = 'conic-gradient(var(--success) ' + pct + '%, var(--border) 0%)';
        else if (score >= 60) circle.style.background = 'conic-gradient(var(--primary) ' + pct + '%, var(--border) 0%)';
        else if (score >= 40) circle.style.background = 'conic-gradient(var(--warning) ' + pct + '%, var(--border) 0%)';
        else if (score >= 20) circle.style.background = 'conic-gradient(var(--orange) ' + pct + '%, var(--border) 0%)';
        else circle.style.background = 'conic-gradient(var(--danger) ' + pct + '%, var(--border) 0%)';

        if (elapsed < duration) requestAnimationFrame(step);
    }

    requestAnimationFrame(step);
}

// ── Animate progress bar ──────────────────────────────────────────
function animateProgressBar() {
    const bar = document.querySelector('.progress-bar-fill');
    if (!bar) return;
    // Width is already set via Thymeleaf; just trigger a reflow
    const width = bar.style.width;
    bar.style.width = '0%';
    requestAnimationFrame(function () {
        requestAnimationFrame(function () {
            bar.style.width = width;
        });
    });
}

// ── AI button loading states ──────────────────────────────────────
function setupAiButtonLoading() {
    document.querySelectorAll('.ai-form').forEach(function (form) {
        form.addEventListener('submit', function (e) {
            const btn = form.querySelector('.btn-ai');
            if (btn) {
                btn.style.opacity = '0.6';
                btn.disabled = true;
                const titleEl = btn.querySelector('.btn-ai-title');
                const subEl = btn.querySelector('.btn-ai-sub');
                if (titleEl) titleEl.textContent = 'Generating...';
                if (subEl) subEl.textContent = '⏳ Calling Groq AI, please wait';
            }
        });
    });
}

// ── Scroll to AI section if hash present ─────────────────────────
function scrollToAiSection() {
    if (window.location.hash === '#ai-section') {
        setTimeout(function () {
            const el = document.getElementById('ai-section');
            if (el) el.scrollIntoView({ behavior: 'smooth', block: 'start' });
        }, 200);
    }
}

// ── Init ──────────────────────────────────────────────────────────
document.addEventListener('DOMContentLoaded', function () {
    animateScoreCircle();
    animateProgressBar();
    setupAiButtonLoading();
    scrollToAiSection();
});
