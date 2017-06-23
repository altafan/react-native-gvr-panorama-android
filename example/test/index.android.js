/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */

import React, { Component } from 'react';
import {
  AppRegistry,
  StyleSheet,
  Dimensions,
  Text,
  View
} from 'react-native';
import PanoramaView from 'react-native-gvr-panorama-android'

const WIDTH = Dimensions.get('window').width

export default class test extends Component {
  render() {
    return (
      <View style={styles.container}>
        <PanoramaView 
          image={{
            uri: 'http://blog.dsky.co/wp-content/uploads/2015/09/06-VikingVillage_stereo_thumb.jpg',
            type: 'stereo'
          }}
          displayMode='embedded'
          enableInfoButton={true}
          hidesTransitionView={true}
          enableTouchTracking={true}
          enableCardboardButton={true}
          enableFullscreenButton={true} 
          style={{ height: 200, width: WIDTH - 20, marginTop: 10}}         
        />
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  }
});

AppRegistry.registerComponent('test', () => test);
