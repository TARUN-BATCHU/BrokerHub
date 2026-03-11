window.APP_CONFIG = {
  API_URL: window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1'
    ? 'http://localhost:8080/BrokerHub'
    : 'http://192.168.0.107:8080/BrokerHub'
};
