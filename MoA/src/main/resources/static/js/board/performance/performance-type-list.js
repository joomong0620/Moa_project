document.addEventListener("DOMContentLoaded", () => {
    const viewMode = document.querySelector('.view-mode');
    const listIcon = viewMode.querySelector('.fa-list');
    const gridIcon = viewMode.querySelector('.bi-grid-fill');

    viewMode.addEventListener('click', () => {
        if (listIcon.style.display !== 'none') {
            listIcon.style.display = 'none';
            gridIcon.style.display = 'inline-block';

        } else {
            listIcon.style.display = 'inline-block';
            gridIcon.style.display = 'none';

        }
    });
});
