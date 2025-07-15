<template>
    <div class="wscn-http404-container">
      <div class="wscn-http404">
        <div class="pic-404">
          <img class="pic-404__parent" src="@/assets/404_images/404.png" alt="404" />
          <img class="pic-404__child left" src="@/assets/404_images/404_cloud.png" alt="404" />
          <img class="pic-404__child mid" src="@/assets/404_images/404_cloud.png" alt="404" />
          <img class="pic-404__child right" src="@/assets/404_images/404_cloud.png" alt="404" />
        </div>
        <div class="bullshit">
          <div class="bullshit__oops">OOPS!</div>
          <div class="bullshit__headline">{{ message }}</div>
          <div class="bullshit__info">Please check that the URL you entered is correct, or click the button below to return to the homepage.</div>
          <a href="/" class="bullshit__return-home">Back to home</a>
        </div>
      </div>
    </div>
</template>

<script>
export default {
  name: "Page404",
  computed: {
    message() {
      return "The webmaster said that you can not enter this page...";
    }
  }
};
</script>

<style lang="scss" scoped>
.wscn-http404-container {
  width: 100%;
  height: 80vh;
  display: flex;
  justify-content: center;
  align-items: center;
}

.wscn-http404 {
  position: relative;
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  align-items: center;

  .pic-404 {
    position: relative;
    width: 100%;
    max-width: 600px;
    padding: 20px;

    &__parent {
      width: 100%;
      display: block;
    }

    &__child {
      position: absolute;
      width: 80px;

      &.left {
        top: 17px;
        left: 220px;
        opacity: 0;
        animation: cloudLeft 2s linear forwards;
        animation-delay: 1s;
      }

      &.mid {
        width: 46px;
        top: 10px;
        left: 420px;
        opacity: 0;
        animation: cloudMid 2s linear forwards;
        animation-delay: 1.2s;
      }

      &.right {
        width: 62px;
        top: 100px;
        left: 500px;
        opacity: 0;
        animation: cloudRight 2s linear forwards;
        animation-delay: 1s;
      }
    }
  }

  .bullshit {
    position: relative;
    width: 100%;
    max-width: 300px;
    padding: 30px 0;
    text-align: center;

    &__oops {
      font-size: clamp(32px, 5vw, 48px);
      font-weight: bold;
      line-height: 1.2;
      color: #1482f0;
      opacity: 0;
      margin-bottom: 20px;
      animation: slideUp 0.5s forwards;
    }

    &__headline {
      font-size: clamp(18px, 3vw, 24px);
      line-height: 1.3;
      color: #222;
      font-weight: bold;
      opacity: 0;
      margin-bottom: 10px;
      animation: slideUp 0.5s forwards;
      animation-delay: 0.1s;
    }

    &__info {
      font-size: clamp(14px, 2vw, 16px);
      line-height: 1.6;
      color: grey;
      opacity: 0;
      margin-bottom: 30px;
      animation: slideUp 0.5s forwards;
      animation-delay: 0.2s;
    }

    &__return-home {
      display: inline-block;
      padding: 10px 30px;
      background: #1482f0;
      border-radius: 50px;
      text-align: center;
      color: #ffffff;
      opacity: 0;
      font-size: 14px;
      line-height: 1.5;
      text-decoration: none;
      cursor: pointer;
      animation: slideUp 0.5s forwards;
      animation-delay: 0.3s;
      transition: background 0.3s;

      &:hover {
        background: #0056b3;
      }
    }
  }
}

@media screen and (max-width: 768px) {
  .wscn-http404 {
    flex-direction: column;
    
    .pic-404 {
      max-width: 100%;
      padding: 10px;

      &__child {
        display: none; // 在移动端隐藏浮动云朵
      }
    }

    .bullshit {
      padding: 15px 0;
    }
  }
}

@keyframes cloudLeft {
  0% {
    top: 17px;
    left: 220px;
    opacity: 0;
  }
  20% {
    top: 33px;
    left: 188px;
    opacity: 1;
  }
  80% {
    top: 81px;
    left: 92px;
    opacity: 1;
  }
  100% {
    top: 97px;
    left: 60px;
    opacity: 0;
  }
}

@keyframes cloudMid {
  0% {
    top: 10px;
    left: 420px;
    opacity: 0;
  }
  20% {
    top: 40px;
    left: 360px;
    opacity: 1;
  }
  70% {
    top: 130px;
    left: 180px;
    opacity: 1;
  }
  100% {
    top: 160px;
    left: 120px;
    opacity: 0;
  }
}

@keyframes cloudRight {
  0% {
    top: 100px;
    left: 500px;
    opacity: 0;
  }
  20% {
    top: 120px;
    left: 460px;
    opacity: 1;
  }
  80% {
    top: 180px;
    left: 340px;
    opacity: 1;
  }
  100% {
    top: 200px;
    left: 300px;
    opacity: 0;
  }
}

@keyframes slideUp {
  0% {
    transform: translateY(60px);
    opacity: 0;
  }
  100% {
    transform: translateY(0);
    opacity: 1;
  }
}
</style>
