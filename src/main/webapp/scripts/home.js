function generateRandomNumbers(){
    let n1 = Math.random() * 50;
    let n2 = Math.random() * 50;
    return {n1, n2, "sum":n1+n2}
}

window.addEventListener(onload, (ev)=>{
    let captcha = document.getElementById("captcha");
    let lable = captcha.firstChild;
    let form = document.getElementById("main_form");
    let input = lable.firstChild;

    let captcha_values = generateRandomNumbers();

    lable.textContent = `Risolvi: ${captcha_values.n1} + ${captcha_values.n2}`

    form.addEventListener(onsubmit, (e)=>{
        if(input.nodeValue != captcha_values.sum) return false;
        //TODO add error message
        return true;
    })  
})