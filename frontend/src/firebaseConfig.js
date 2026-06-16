import { initializeApp } from "firebase/app";
import { getAuth, GoogleAuthProvider, OAuthProvider, RecaptchaVerifier, signInWithPopup, signInWithPhoneNumber } from "firebase/auth";

const firebaseConfig = {
  apiKey: "AIzaSyAC8UJvfPxMwuu15gaTZZiGy7Uhsfecxuw",
  authDomain: "wattever-131b9.firebaseapp.com",
  projectId: "wattever-131b9",
  storageBucket: "wattever-131b9.firebasestorage.app",
  messagingSenderId: "928615545991",
  appId: "1:928615545991:web:9887fefaf3449b7e3b4c22",
  measurementId: "G-FQJLFE38E7"
};

const app = initializeApp(firebaseConfig);
export const auth = getAuth(app);
export const googleProvider = new GoogleAuthProvider();
export const appleProvider = new OAuthProvider('apple.com');

export const setupRecaptcha = (containerId) => {
  return new RecaptchaVerifier(auth, containerId, {
    size: 'invisible'
  });
};

export { signInWithPopup, signInWithPhoneNumber };
