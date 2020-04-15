//package com.daemonize.game;
//
//import com.daemonize.daemonengine.consumer.Consumer;
//import com.daemonize.daemonengine.dummy.DummyDaemon;
//import com.daemonize.daemonprocessor.Pair;
//import com.daemonize.graphics2d.camera.Camera2D;
//import com.daemonize.graphics2d.camera.FixedCamera;
//
//public class CameraFixingSwitcher implements CameraSwitcher<CameraFixingSwitcher>{
//
//    private Consumer consumeer;
//
//    private DummyPlayerDaemon playerFollower;
//
//    private Camera2D currentCamera;
//    private Camera2D originalCamera;
//    private final FixedCamera fixedCamera;
//
//    //player view map
//    //player animate closure
//
//    public CameraFixingSwitcher(Consumer consumer, Camera2D originalCamera, float dXY) {
//        this.consumeer = consumer;
//        this.originalCamera = originalCamera;
//        this.currentCamera = originalCamera;
//        this.fixedCamera = new FixedCamera(0, 0, originalCamera.getWidth(), originalCamera.getHeight());
//        this.playerFollower = new DummyPlayerDaemon(consumer, new DummyPlayer( null, Pair.create(), dXY));
//    }
//
//    public CameraFixingSwitcher(Consumer consumer, float dXY) {
//        this(consumer, null, dXY);
//    }
//
//    @Override
//    public CameraFixingSwitcher setCamera(Camera2D camera) {
//
//        if (this.currentCamera == this.originalCamera)
//            this.currentCamera = camera;
//
//        this.originalCamera = camera;
//        return this;
//    }
//
//    @Override
//    public void switchCameras() {
//
//                        if (currentCamera.equals(originalCamera)) {
//
//                            playerAnimateClosure.setMainView(playerViewMap.get("mainFC"))
//                                    .setHpView(playerViewMap.get("hpFC"))
//                                    .setSearchlightView(playerViewMap.get("searchlightFC"));
//
//                            fixedCamera.setX(followingCamera.getRenderingX())
//                                    .setY(followingCamera.getRenderingY());
//
//                            playerViewMap.get("mainFC")
//                                    .setAbsoluteX(playerViewMap.get("main").getAbsoluteX())
//                                    .setAbsoluteY(playerViewMap.get("main").getAbsoluteY())
//                                    .setImage(playerViewMap.get("main").getImage());
//
//                            playerViewMap.get("hpFC").setAbsoluteX(playerViewMap.get("hp").getAbsoluteX())
//                                    .setAbsoluteY(playerViewMap.get("hp").getAbsoluteY())
//                                    .setImage(playerViewMap.get("hp").getImage());
//
//                            playerViewMap.get("searchlightFC").setAbsoluteX(playerViewMap.get("searchlightView").getAbsoluteX())
//                                    .setAbsoluteY(playerViewMap.get("searchlightView").getAbsoluteY())
//                                    .setImage(playerViewMap.get("searchlightView").getImage());
//
//                            renderer.consume(() -> {
//                                playerViewMap.get("main").hide();
//                                playerViewMap.get("hp").hide();
//                                playerViewMap.get("searchlightView").hide();
//
//                                playerViewMap.get("mainFC").show();
//                                playerViewMap.get("hpFC").show();
//                                playerViewMap.get("searchlightFC").show();
//                            });
//
//                            setCamera(fixedCamera);
//
//                        } else {
//
//                            dummyPlayer.setCoordinates(fixedCamera.getCenterX(), fixedCamera.getCenterY());
//                            setCamera(followingCamera.setTarget(dummyPlayer));
//
//                            dummyPlayer.goTo(
//                                    player.getLastCoordinates(),
//                                    35,
//                                    new Runnable() {
//                                        @Override
//                                        public void run() {
//
//                                            if (Math.abs(dummyPlayer.getLastCoordinates().getFirst() - player.getLastCoordinates().getFirst()) > 30
//                                                    && Math.abs(dummyPlayer.getLastCoordinates().getSecond() - player.getLastCoordinates().getSecond()) > 25) {
//                                                dummyPlayer.goTo(player.getLastCoordinates(), 20, this::run);
//                                                return;
//                                            }
//
//                                            playerViewMap.get("main")
//                                                    .setAbsoluteX(playerViewMap.get("mainFC").getAbsoluteX())
//                                                    .setAbsoluteY(playerViewMap.get("mainFC").getAbsoluteY())
//                                                    .setImage(playerViewMap.get("mainFC").getImage());
//
//                                            playerViewMap.get("hp")
//                                                    .setAbsoluteX(playerViewMap.get("hpFC").getAbsoluteX())
//                                                    .setAbsoluteY(playerViewMap.get("hpFC").getAbsoluteY())
//                                                    .setImage(playerViewMap.get("hpFC").getImage());
//
//                                            playerViewMap.get("searchlightView")
//                                                    .setAbsoluteX(playerViewMap.get("searchlightFC").getAbsoluteX())
//                                                    .setAbsoluteY(playerViewMap.get("searchlightFC").getAbsoluteY())
//                                                    .setImage(playerViewMap.get("searchlightFC").getImage());
//
//                                            playerAnimateClosure.setMainView(playerViewMap.get("main"))
//                                                        .setHpView(playerViewMap.get("hp"))
//                                                        .setSearchlightView(playerViewMap.get("searchlightView"));
//
//                                            followingCamera.setTarget(player);
//
//                                            renderer.consume(() -> {
//
//                                                playerViewMap.get("mainFC").hide();
//                                                playerViewMap.get("hpFC").hide();
//                                                playerViewMap.get("searchlightFC").hide();
//
//                                                playerViewMap.get("main").show();
//                                                playerViewMap.get("hp").show();
//                                                playerViewMap.get("searchlightView").show();
//
//                                            });
//                                        }
//                                    });
//                        }
//        }
//
//
//
//    }
//
//    //
////                dummyPlayer = new DummyPlayerDaemon(gameConsumer, new DummyPlayer(playerSprite[0], player.getLastCoordinates(), dXY));
////                dummyPlayer.setAnimateDummyPlayerSideQuest(renderer).setClosure(ret -> {});
////                dummyPlayer.start();
//
//    //camera switcher init
////                cameraSwitcher = DummyDaemon.create(gameConsumer, 5000L).setClosure(new Runnable() {
////
////                    private Camera2D currentCamera = followingCamera;
////
////                    private void setCamera(Camera2D camera) {
////                        this.currentCamera = camera;
////                        renderer.setCamera(camera);
////                    }
////
////                    @Override
////                    public void run() {
////
////                        if (currentCamera.equals(followingCamera)) {
////
////                            playerAnimateClosure.setMainView(playerViewMap.get("mainFC"))
////                                    .setHpView(playerViewMap.get("hpFC"))
////                                    .setSearchlightView(playerViewMap.get("searchlightFC"));
////
////                            fixedCamera.setX(followingCamera.getRenderingX())
////                                    .setY(followingCamera.getRenderingY());
////
////                            playerViewMap.get("mainFC")
////                                    .setAbsoluteX(playerViewMap.get("main").getAbsoluteX())
////                                    .setAbsoluteY(playerViewMap.get("main").getAbsoluteY())
////                                    .setImage(playerViewMap.get("main").getImage());
////
////                            playerViewMap.get("hpFC").setAbsoluteX(playerViewMap.get("hp").getAbsoluteX())
////                                    .setAbsoluteY(playerViewMap.get("hp").getAbsoluteY())
////                                    .setImage(playerViewMap.get("hp").getImage());
////
////                            playerViewMap.get("searchlightFC").setAbsoluteX(playerViewMap.get("searchlightView").getAbsoluteX())
////                                    .setAbsoluteY(playerViewMap.get("searchlightView").getAbsoluteY())
////                                    .setImage(playerViewMap.get("searchlightView").getImage());
////
////                            renderer.consume(() -> {
////                                playerViewMap.get("main").hide();
////                                playerViewMap.get("hp").hide();
////                                playerViewMap.get("searchlightView").hide();
////
////                                playerViewMap.get("mainFC").show();
////                                playerViewMap.get("hpFC").show();
////                                playerViewMap.get("searchlightFC").show();
////                            });
////
////                            setCamera(fixedCamera);
////
////                        } else {
////
////                            dummyPlayer.setCoordinates(fixedCamera.getCenterX(), fixedCamera.getCenterY());
////                            setCamera(followingCamera.setTarget(dummyPlayer));
////
////                            dummyPlayer.goTo(
////                                    player.getLastCoordinates(),
////                                    35,
////                                    new Runnable() {
////                                        @Override
////                                        public void run() {
////
////                                            if (Math.abs(dummyPlayer.getLastCoordinates().getFirst() - player.getLastCoordinates().getFirst()) > 30
////                                                    && Math.abs(dummyPlayer.getLastCoordinates().getSecond() - player.getLastCoordinates().getSecond()) > 25) {
////                                                dummyPlayer.goTo(player.getLastCoordinates(), 20, this::run);
////                                                return;
////                                            }
////
////                                            playerViewMap.get("main")
////                                                    .setAbsoluteX(playerViewMap.get("mainFC").getAbsoluteX())
////                                                    .setAbsoluteY(playerViewMap.get("mainFC").getAbsoluteY())
////                                                    .setImage(playerViewMap.get("mainFC").getImage());
////
////                                            playerViewMap.get("hp")
////                                                    .setAbsoluteX(playerViewMap.get("hpFC").getAbsoluteX())
////                                                    .setAbsoluteY(playerViewMap.get("hpFC").getAbsoluteY())
////                                                    .setImage(playerViewMap.get("hpFC").getImage());
////
////                                            playerViewMap.get("searchlightView")
////                                                    .setAbsoluteX(playerViewMap.get("searchlightFC").getAbsoluteX())
////                                                    .setAbsoluteY(playerViewMap.get("searchlightFC").getAbsoluteY())
////                                                    .setImage(playerViewMap.get("searchlightFC").getImage());
////
////                                            playerAnimateClosure.setMainView(playerViewMap.get("main"))
////                                                        .setHpView(playerViewMap.get("hp"))
////                                                        .setSearchlightView(playerViewMap.get("searchlightView"));
////
////                                            followingCamera.setTarget(player);
////
////                                            renderer.consume(() -> {
////
////                                                playerViewMap.get("mainFC").hide();
////                                                playerViewMap.get("hpFC").hide();
////                                                playerViewMap.get("searchlightFC").hide();
////
////                                                playerViewMap.get("main").show();
////                                                playerViewMap.get("hp").show();
////                                                playerViewMap.get("searchlightView").show();
////
////                                            });
////                                        }
////                                    });
////                        }
////                    }
////                });
//
