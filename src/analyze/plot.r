
#Tag distribution plot
library(ggplot2)
tags<-read.csv(file='data/Tags',header=FALSE)
num <- 1:nrow(tags)
counts<- tags[[2]]


jpeg("tag-dist.jpg")
plot(num,counts,log='x',type='l',col='#598709',main="Rank vs. Frequency", xlab="Tag Rank", ylab="Tag Frequency")
dev.off()

#Count distribution plot

tag <- matrix(nrow=5,ncol =2)
tag[1,1]<-1
tag[2,1]<-2
tag[3,1]<-3
tag[4,1]<-4
tag[5,1] <-5
tag[1,2] <-590842
tag[2,2] <-1300216
tag[3,2] <-1484189
tag[4,2] <-1041552
tag[5,2] <-721309

tag <- data.frame(tag)

ggplot(data = tag,aes(x=tag$X1,y=tag$X2))+geom_bar(stat = 'identity',fill = "orange")+labs(x="Tag count",y="Questions",ggtitle="Tag count for all the Questions") + scale_x_continuous(breaks = seq(0, 1500000, 50000))
tplot<-ggplot(data = tag,aes(x=tag$X1,y=tag$X2))+geom_bar(stat = 'identity',fill = "orange")+labs(x="Tag count",y="Questions",title="Number of Tags per Question - Distribution") + scale_y_continuous(breaks = seq(0, 1600000, 200000))

ggsave(filename = "count-dist.png",plot=tplot)

