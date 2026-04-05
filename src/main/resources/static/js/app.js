// ── File Upload Handling ──────────────────────────────────────────
function setupFileInput(inputId, dropZoneId, contentId, infoId) {
    const input = document.getElementById(inputId);
    const dropZone = document.getElementById(dropZoneId);
    const info = document.getElementById(infoId);

    if (!input) return;

    input.addEventListener('change', function () {
        if (this.files.length > 0) {
            showFileInfo(this.files[0], info, dropZone);
        }
    });

    if (!dropZone) return;

    dropZone.addEventListener('dragover', function (e) {
        e.preventDefault();
        dropZone.classList.add('drag-over');
    });

    dropZone.addEventListener('dragleave', function () {
        dropZone.classList.remove('drag-over');
    });

    dropZone.addEventListener('drop', function (e) {
        e.preventDefault();
        dropZone.classList.remove('drag-over');
        const files = e.dataTransfer.files;
        if (files.length > 0) {
            input.files = files;
            showFileInfo(files[0], info, dropZone);
        }
    });
}

function showFileInfo(file, infoEl, dropZone) {
    if (!infoEl) return;
    const sizeMB = (file.size / 1024 / 1024).toFixed(2);
    infoEl.innerHTML = '✅ <strong>' + escapeHtml(file.name) + '</strong> (' + sizeMB + ' MB)';
    infoEl.classList.remove('hidden');
    if (dropZone) {
        dropZone.style.borderColor = 'var(--success)';
        dropZone.style.background = 'var(--success-light)';
    }
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.appendChild(document.createTextNode(text));
    return div.innerHTML;
}

// ── Form Submit Loading ───────────────────────────────────────────
function setupFormLoading() {
    const form = document.getElementById('uploadForm');
    const btn = document.getElementById('analyzeBtn');
    const btnText = document.getElementById('btnText');
    const btnLoading = document.getElementById('btnLoading');

    if (!form) return;

    form.addEventListener('submit', function (e) {
        const resumeFile = document.getElementById('resumeFile');
        const jdFile = document.getElementById('jdFile');
        const jdText = document.getElementById('jdText');

        if (!resumeFile || resumeFile.files.length === 0) {
            e.preventDefault();
            alert('Please upload your resume file (PDF or DOCX).');
            return;
        }

        const hasJdFile = jdFile && jdFile.files.length > 0;
        const hasJdText = jdText && jdText.value.trim().length > 0;

        if (!hasJdFile && !hasJdText) {
            e.preventDefault();
            alert('Please provide a job description — either upload a file or paste the text.');
            return;
        }

        // Show loading
        if (btnText) btnText.classList.add('hidden');
        if (btnLoading) btnLoading.classList.remove('hidden');
        if (btn) btn.disabled = true;

        // Show overlay
        showLoadingOverlay('🔍 Parsing documents and extracting skills...');
    });
}

// ── Loading Overlay ───────────────────────────────────────────────
function showLoadingOverlay(message) {
    let overlay = document.getElementById('loadingOverlay');
    if (!overlay) {
        overlay = document.createElement('div');
        overlay.id = 'loadingOverlay';
        overlay.className = 'loading-overlay';
        overlay.innerHTML = '<div class="spinner"></div><div class="loading-text" id="loadingText">' + escapeHtml(message) + '</div>';
        document.body.appendChild(overlay);
    }
    overlay.classList.add('active');
    return overlay;
}

// ── Init ──────────────────────────────────────────────────────────
document.addEventListener('DOMContentLoaded', function () {
    setupFileInput('resumeFile', 'resumeDropZone', 'resumeContent', 'resumeInfo');
    setupFileInput('jdFile', 'jdDropZone', 'jdContent', 'jdInfo');
    setupFormLoading();
});
