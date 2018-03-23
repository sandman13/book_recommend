function solveSuccess(message) {
    $.notify({
        icon: "glyphicon glyphicon-ok",
        message: message,
    }, {
        type: "success",
        allow_dismiss: true,
        placement: {
            from: "top",
            align: "center"
        },
        offset: 200,
        spacing: 10,
        delay: 500,
        timer: 500,
        z_index: 1093, <!--控制有多个元素在同一个位置悬浮的高度-->
        url_target: '_blank',
        mouse_over: null,
        animate: {
            enter: "animated bounceIn", <!--动画需要引入自己的css文件-->
            exit: "animated bounceOut"
        },
        onShow: null,
        onShown: null,
        onClose: null,
        onClosed: null
    });
}

function solveFail(message) {
    $.notify({
        icon: "glyphicon glyphicon-remove",
        message: message,
    }, {
        type: "danger",
        allow_dismiss: true,
        placement: {
            from: "top",
            align: "center"
        },
        offset: 200,
        spacing: 10,
        delay: 500,
        timer: 500,
        z_index: 1093, <!--控制有多个元素在同一个位置悬浮的高度-->
        url_target: '_blank',
        mouse_over: null,
        animate: {
            enter: "animated bounceIn", <!--动画需要引入自己的css文件-->
            exit: "animated bounceOut"
        },
        onShow: null,
        onShown: null,
        onClose: null,
        onClosed: null
    });
}
