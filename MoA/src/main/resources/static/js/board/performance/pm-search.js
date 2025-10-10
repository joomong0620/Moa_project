document.querySelector('.apply-btn').addEventListener('click', () => {
    const params = new URLSearchParams();

    document.querySelectorAll('.filter-group').forEach(group => {
        const key = group.dataset.filter; // genre, price, period, region...
        const selected = [...group.querySelectorAll('input[type="checkbox"]:checked')]
            .map(el => el.dataset.value)
            .filter(v => v !== 'all');

        // 다중 선택을 반복 키로 추가
        selected.forEach(v => params.append(key, v));
    });

    const keyword = document.querySelector('.search-bar input')?.value.trim();
    if (keyword) params.set('q', keyword);

    window.location.href = `/search?${params.toString()}`;
});
