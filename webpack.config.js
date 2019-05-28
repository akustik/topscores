const path = require('path');
const webpack = require('webpack')

module.exports = {
  entry: './src/main/js/app.js',
  devtool: 'sourcemaps',
  cache: true,
  mode: 'development',
  output: {
    path: __dirname,
    filename: './src/main/resources/static/built/bundle.js'
  },
  plugins: [
    new webpack.ProvidePlugin({
      $: 'jquery',
      jQuery: 'jquery'
    })
  ],
  module: {
    rules: [
      {
        test: /\.(ttf|eot|svg|woff(2)?)(\?[a-z0-9]+)?$/,
        loader: 'file-loader',
        options: {
          outputPath: './src/main/resources/static/built/fonts/'
        }
      },
      {
        test: /\.css$/,
        use: ['style-loader', 'css-loader']
      },
          
      {
        test: path.join(__dirname, '.'),
        exclude: /(node_modules)/,
        use: [{
          loader: 'babel-loader',
          options: {
            presets: ["@babel/preset-env", "@babel/preset-react"],
            plugins: [
              "@babel/plugin-proposal-class-properties"
            ]
          }
        }]
      }
    ]
  }
};
