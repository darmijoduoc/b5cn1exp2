// =========================================================
// Semana 3 – Archivo de entorno de desarrollo
// =========================================================
//
// Aquí declaramos variables globales que Angular usará
// en toda la aplicación.
//
// La más importante: la URL base del microservicio backend.
// =========================================================

export const environment = {
  // Dirección base del backend (Spring Boot)
  //apiBaseUrl: 'https://652w7c49f3.execute-api.us-east-1.amazonaws.com/',
  apiBaseUrl: 'http://23.21.0.38:8081/api/bff',

  // Indica que estamos en entorno de desarrollo
  production: false,
  
  msalConfig: {
    auth: {
      clientId: '18687dd3-8a42-4a55-b105-0797e1bfa165',
      authority: 'https://login.microsoftonline.com/common',
    },
  },
  apiConfig: {
    scopes: ['User.Read'],
    uri: 'https://graph.microsoft.com/v1.0/me',
  },
};

