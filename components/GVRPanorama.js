/*
 * @Author: altafan 
 * @Date: 2017-06-22 15:25:03 
 * @Last Modified by: altafan
 * @Last Modified time: 2017-06-23 17:43:18
 */
import React, { Component, PropTypes } from 'react'
import { requireNativeComponent, View } from 'react-native'


class PanoramaView extends Component {
  render() {
    return <RCTPanoramaView {...this.props} />
  }
}

PanoramaView.propTypes = {
  ...View.propTypes,
  image: PropTypes.shape({
    uri: PropTypes.string,
    type: PropTypes.string
  }).isRequired,
  displayMode: PropTypes.string,
  enableInfoButton: PropTypes.bool,
  enableTouchTracking: PropTypes.bool,
  hidesTransitionView: PropTypes.bool,
  enableCardboardButton: PropTypes.bool,
  enableFullscreenButton: PropTypes.bool,
}

const RCTPanoramaView = requireNativeComponent('Panorama', PanoramaView, { nativeOnly: {} });

export default PanoramaView;