import { View, Text, ScrollView, StyleSheet, Dimensions, StyleProp, ViewStyle, Image, TouchableOpacity, Pressable } from 'react-native';
import { colors } from '../assets/colors.tsx'
import React, { useCallback, useContext, useEffect, useState } from 'react';
import ImageWithIconOverlay from '../components/ImageWithIconOverlay.tsx';
import CarouselSlider from '../components/CarouselSlider.tsx';
import SelectableTag from '../components/SelectableTag.tsx';

import { getCategoryUser, getGoodCombi, isErrorResponse, isResumeResponse, isResumesResponse } from '../util/auth.tsx';
import { AuthContext } from "../store/auth-context.tsx";

// 이미지들의 고유 key를 임시로 주기 위한 라이브러리
import 'react-native-get-random-values';
import { FlatList } from 'react-native-gesture-handler';
import { Category, category as categoryForm } from '../util/categoryFormat.tsx';
import Config from 'react-native-config';
import axios from 'axios';
import { createAlertMessage } from '../util/alert.tsx';
import { StompClientContext } from '../store/socket-context.tsx';
import { UserContext } from '../store/user-context.tsx';
import { useFocusEffect } from '@react-navigation/native';
import CustomButton from '../components/CustomButton.tsx';


const Friends = ({navigation}: any) => {
  interface Content {
    resumeId: number;
    thumbnailS3url: string
  }
  interface Faces {
    [key: string]: {content: Content[], last: boolean}
  }

  // auth를 위한 method
  const authCtx = useContext(AuthContext);
  const userCtx = useContext(UserContext);

  // CarouselSlider의 필수 파라미터, pageWidth, offset, gap 설정
  const pageWidth = Dimensions.get('window').width;
  const offset = 0;
  const [ gap, setGap ] = useState({leftGap: 0, rightGap: 0, topGap: 0, bottomGap: 0});

  // 현재 이미지 슬라이더의 페이지를 알기 위한 페이지 설정
  const [ page, setPage ] = useState(0);

  // 이미지 슬라이더의 내부 contents 설정
  const [ images, setImages ] = useState([
    {
      id: 1,
      source: require('../assets/images/banner1.png'),
      onPress: () => {navigation.navigate('Banner1')}
    },
    {
      id: 2,
      source: require('../assets/images/banner2.png'),
      onPress: () => {navigation.navigate('Banner2')}
    },
    {
      id: 3,
      source: require('../assets/images/banner3.png'),
      onPress: () => {navigation.navigate('Banner3')}
    },
  ]);

  // 추천 얼굴 이미지 data
  const [faces, setFaces] = useState<Faces>({
    FIT: {content: [], last: false}
  });

  /**
   * 이미지 슬라이더에 들어갈 컨텐츠 내용물 데이터를 React.ReactNode로 바꿔주는 함수
   * @param param0 :any
   * @param containerStyle :이미지 슬라이더가 gap, offset 등을 설정할 수 있게, style을 파라미터로 받아서, 알맞은 ReactNode에 적용
   * @returns React.ReactNode
   */
  function renderItem({id, source, onPress}: any,
  containerStyle: StyleProp<ViewStyle>) {
    return (
      <TouchableOpacity onPress={onPress}>
        <ImageWithIconOverlay
          source={source} borderRadius={0} key={id}
          containerStyle={containerStyle}/>
      </TouchableOpacity>
    );
  }

  const tryGetGoodCombi = async () => {
    if (authCtx.accessToken) {
      const response = await getGoodCombi(
        authCtx.accessToken, 0, 10
      )
      if (isResumesResponse(response)) {
        setFaces((prev) => ({
          ...prev,
          "FIT": {content: response.content, last: response.last}
        }))
      }
      if (isErrorResponse(response)) {
        console.log("error")
      }
    }
  }

  const tryGetCategoryUser = async (category: string) => {
    if (authCtx.accessToken) {
      const response = await getCategoryUser(
        authCtx.accessToken, 0, 10, category
      )
      if (isResumesResponse(response)) {
        setFaces((prev) => ({
          ...prev,
          [category]: {content: response.content, last: response.last}
        }))
      }
      if (isErrorResponse(response)) {
        console.log("error")
      }
    }
  }

  const fetchNewData = async (type: string) => {
    if (!(type in faces)) return;
    if (faces[`${type}`].last) return;

    if (authCtx.accessToken) {
      var response: any;
      if (type === "FIT") {
        response = await getGoodCombi(
          authCtx.accessToken,
          faces[`${type}`].content.length/10,
          10
        );
      } else {
        response = await getCategoryUser(
          authCtx.accessToken,
          faces[`${type}`].content.length/10,
          10, type
        );
      }

      if (isResumesResponse(response)){
        setFaces((prev) => ({
          ...prev,
          [type]: {
            content: [...prev[`${type}`].content, ...response.content], 
            last: response.last
          }
        }));
      }
    }
  };

  const renderCardItem = ({item}: {item: Content}) => {{
    return (
      <TouchableOpacity 
        key={item.resumeId} 
        style={{marginHorizontal: 10}} 
        onPress={() => navigation.navigate("OtherSelfProduce", {resumeId: item.resumeId})}>
        <Image 
          source={{uri: item.thumbnailS3url}} 
          style={{borderRadius:16, borderWidth: 1, borderColor: colors.pastel_point}} 
          width={150} height={150}
        />
      </TouchableOpacity>);
  }}

  const categoriesText = [["맛집 탐방 같이 하실 분", 'FOOD'], ["탁구하러 가실 분", "WORKOUT"], ["듄 함께 보실 분~", "MOVIE"], ["패션 참견 해주실 분99", "FASHION"], ["연애 상담 해드립니다~!", "DATING"], ["팝송 러버 여기 모여라", "MUSIC"], ["치타는 웃고 있다", "STUDY"], ["심심한데 이야기하실 분", "ETC"]];

  useEffect(() => {
    if (userCtx.resumeinfo) {
      setFaces({FIT: {content: [], last: false}})
      for (const category of userCtx.resumeinfo.categories) {
        tryGetCategoryUser(category);
      }
      tryGetGoodCombi();
    } else {
      setFaces({FIT: {content: [], last: false}})
    }
  }, [userCtx.resumeinfo])

  return (
    <ScrollView showsVerticalScrollIndicator={false} contentContainerStyle={{backgroundColor: colors.white, minHeight: '100%'}}>
      {/* 이미지 슬라이더 */}
      <View>
        <CarouselSlider
          pageWidth={pageWidth}
          autoScrollToNextPage
          autoScrollToNextPageInterval={3000}
          pageHeight={pageWidth}
          offset={offset}
          gap={gap}
          data={images}
          onPageChange={setPage}
          renderItem={renderItem}/>
      </View>
      <View style={{flexDirection: 'row', alignSelf: 'center', paddingTop: 10}}>
      {
        images.map((item, idx) => {
          return (
            <View 
              key={idx}
              style={{marginHorizontal: 4, width: 8, height: 8, borderRadius: 50,
              backgroundColor: (idx == page) ? colors.pastel_point : colors.gray2}}/>
          );
        })
      }
      </View>
      {(userCtx.resumeinfo !== undefined) ? <>
        <View>
          <View style={styles.personalRecommendTop}>
            <View style={styles.sectionTitleContainer}>
              <Text style={styles.sectionTitle}>나와 잘 맞는 관상</Text>
              <View style={{flex: 1}}/>
              <TouchableOpacity onPress={() => {navigation.navigate("TotalRecommend", {type: "FIT", title: "나와 잘 맞는 관상", text: `AI가 분석한 ${userCtx.basicinfo.nickname}님의 베스트 매치 관상 추천`})}}>
                <Text style={[styles.sectionText, { color: colors.gray6 }]}>전체 보러가기{">"}</Text>
              </TouchableOpacity>
            </View>
            <Text style={[{paddingLeft: 27}, styles.sectionText]}>AI가 분석한 {userCtx.basicinfo.nickname}님의 베스트 매치 관상 추천</Text>
          </View>
        </View>
        <FlatList 
          horizontal 
          data={faces.FIT.content} 
          renderItem={renderCardItem}
          style={{paddingVertical: 26, paddingHorizontal: 16}}
          onEndReached={() => fetchNewData("FIT")}/>
        {Object.keys(faces).length > 1 ?
          <View style={{backgroundColor: colors.white, paddingTop: 20}}>
            <Text style={styles.categorySectionTitle}>카테고리별 맞춤 추천</Text>
            {categoriesText.map(([text, tag], idx) => {
              if (faces[`${tag}`]) {
                return (
                  <View key={idx}>
                    <View style={{marginHorizontal: 26, flexDirection: 'row', alignItems: 'center'}}>
                      <SelectableTag height={27} textStyle={{fontSize: 16, color: colors.white, fontFamily: 'Pretendard-Medium', letterSpacing: -16*0.02}} containerStyle={{backgroundColor: colors.point, borderColor: colors.point}}>{categoryForm[tag as keyof Category]}</SelectableTag>
                      {/* <Text style={[{paddingLeft: 8}, styles.sectionText]}>{text}</Text> */}
                      <View style={{flex: 1}}/>
                      <TouchableOpacity onPress={() => {navigation.navigate("TotalRecommend", {type: tag, title: categoryForm[tag as keyof Category], text: '카테고리별 맞춤 추천'})}}>
                        <Text style={[styles.sectionText, { color: colors.gray6 }]}>전체 보러가기{">"}</Text>
                      </TouchableOpacity>
                    </View>
                    <FlatList 
                      horizontal 
                      data={faces[tag as keyof Category]?.content} 
                      renderItem={renderCardItem}
                      style={{paddingVertical: 26, paddingHorizontal: 16}}
                      onEndReached={() => fetchNewData(tag)}/>
                  </View>
                );
              }
            })
            }
          </View> : <></>
        }</>
        : 
        <View style={{flex: 1, paddingTop: 20, paddingHorizontal: 32, alignItems: 'center', justifyContent: 'center'}}>
          <Text style={styles.sectionText}>자기소개서를 작성하시면, 유저를 추천해드립니다.</Text>
          <View style={{flex: 1}}/>
          <View style={styles.bottomContainer}>
            <CustomButton 
              onPress={()=>{navigation.navigate('sub2');}}
              containerStyle={styles.pointButton} 
              textStyle={styles.buttonText}>
                자기소개서 화면으로 이동하기
            </CustomButton>
          </View>
        </View>
      }
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  sectionTitle: {
    fontSize: 20,
    color: colors.point,
    fontFamily: "Pretendard-SemiBold",
    letterSpacing: -20* 0.02,
  },
  sectionText: {
    fontFamily: "Pretendard-Regular", 
    fontSize: 14, 
    letterSpacing: -14* 0.02,
    color: colors.gray7,
  },
  sectionTitleContainer: {
    marginHorizontal: 26, 
    marginBottom: 8, 
    flexDirection: 'row',
    borderColor: colors.white,
  },
  categorySectionTitle: {
    paddingLeft: 27,
    paddingBottom: 18, 
    fontSize: 20, 
    color: colors.point,
    fontFamily: "Pretendard-SemiBold",
    letterSpacing: -20* 0.02,
  },
  personalRecommendTop: {
    borderBottomWidth: 1, 
    marginTop: 38, 
    paddingBottom: 11, 
    borderColor: colors.white,
  },
  bottomContainer: {
    alignItems: "center",
    marginBottom: 23,
    paddingHorizontal: 3,
  },
  pointButton: {
    backgroundColor: colors.point, 
    marginHorizontal: 5, 
    elevation: 4
  }, 
  buttonText: {
    color: colors.white, 
    fontSize:18, 
    letterSpacing: -18* 0.02, 
    fontFamily: "Pretendard-SemiBold"
  }
});

export default Friends;