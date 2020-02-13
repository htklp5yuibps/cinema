export function makeRequest(url, method = 'GET', contentType = 'application/x-www-form-urlencoded', params = null) {
    return new Promise((resolve) => {
        const xhr = new XMLHttpRequest();
        let paramsString =  '';
        xhr.withCredentials = true;

        xhr.onreadystatechange = () => {
            if (xhr.readyState === XMLHttpRequest.DONE) {
                resolve({
                    status: xhr.status,
                    responseJSON: xhr.responseText ? JSON.parse(xhr.responseText) : ''
                });
            }
        }
        
        xhr.open(method, url);

        if (contentType) {
            xhr.setRequestHeader('Content-Type', contentType);
        }

        if (params) {
            for (let param in params) {
                paramsString += `&${param}=${params[param]}`;
            }
            paramsString.replace('&', '');
        }

        xhr.send(paramsString);
    });
}