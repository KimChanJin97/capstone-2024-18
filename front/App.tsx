import { SafeAreaProvider, SafeAreaView } from 'react-native-safe-area-context';
import { Routes, Route,  NativeRouter } from "react-router-native";
import AuthContextProvider from './store/auth-context';
import Login from './pages/Login.tsx';
import Signup from './pages/Signup.tsx';
import Test1 from './test/pages/Test1.tsx';
import FindEmail from './pages/FindEmail.tsx';
import FindPw from './pages/FindPw.tsx';
import ResetPw from './pages/ResetPw.tsx';

function App() {
  return (
    <AuthContextProvider>
      <SafeAreaProvider>
        <NativeRouter>
          <Routes>
            <Route path={"/"} element={<SafeAreaView><Login/></SafeAreaView>}/>
            <Route path={"/signup"} element={<SafeAreaView><Signup/></SafeAreaView>}/>
            <Route path={"/findemail"} element={<SafeAreaView><FindEmail/></SafeAreaView>}/>
            <Route path={"/findpw"} element={<SafeAreaView><FindPw/></SafeAreaView>}/>
            <Route path={"/resetpw"} element={<SafeAreaView><ResetPw/></SafeAreaView>}/>
            <Route path={'/main'} element={<Test1/>}/>
            {/* Bottom Navigation이 있는 페이지의 경우 SafeAreaView를 이용하면 ios에서 bottomNavigation이 제대로 안 보임 */}
          </Routes>
        </NativeRouter>
    </SafeAreaProvider>  
    </AuthContextProvider>
  );
}

export default App;
